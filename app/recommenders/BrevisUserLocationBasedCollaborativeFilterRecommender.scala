package recommenders

import com.google.inject.Inject
import db.UserLocationDAO
import models.{BrevisContent, BrevisSimilarContent, BrevisUserLocation}

import scala.collection.immutable.HashSet
import scala.collection.mutable
import play.api.Logger

/**
  * Created by connor.jennings on 3/24/17.
  */

case class BrevisSimilarUser(
  userId: String,
  score: Double
)


// Would like to expand on the paper below, but for the moment a much simple/naive implementation:
// https://www.microsoft.com/en-us/research/wp-content/uploads/2016/02/Mining20user20similarity20based20on20location20history.pdf
class BrevisUserLocationBasedCollaborativeFilterRecommender @Inject() ()  {
  protected def countSimilarity(
    userLocationsA: Seq[String],
    userLocationsB: Seq[String]
  ): Double = {
    val userACluster = createClusters(userLocationsA)
    val userBCluster = createClusters(userLocationsB)

    val combined = Set() ++ userLocationsA ++ userLocationsB

    val totalLocations: Double = userLocationsA.size * userLocationsB.size
    combined.foldLeft(0.0)((acc, cluster) => {
      if (userACluster.contains(cluster) && userBCluster.contains(cluster)) {
        acc + Math.min(userACluster(cluster) / totalLocations, userBCluster(cluster) / totalLocations)
      } else {
        acc
      }
    })
  }

  // Naive cosine similarity computation
  protected def cosineSimilarity(
    userLocationsA: Seq[String],
    userLocationsB: Seq[String]): Double = {

    // Take each user's locations, convert them to a map (Location -> # total at that location)
    val userACluster = createClusters(userLocationsA)
    val userBCluster = createClusters(userLocationsB)

    // Combine them all
    val combined = Set() ++ userLocationsA ++ userLocationsB

    val docAVector = combined.foldLeft(List.empty[Double])((acc, feature) => {
      acc :+ userACluster.getOrElse(feature, 0.0)
    })

    val docBVector = combined.foldLeft(List.empty[Double])((acc, feature) => {
      acc :+ userBCluster.getOrElse(feature, 0.0)
    })

    val numerator = (docAVector zip docBVector).map((pair) => pair._1 * pair._2).sum
    val denominator = Math.sqrt(docAVector.map((a) => a * a).sum) * Math.sqrt(docBVector.map((b) => b * b).sum)

    numerator / denominator
  }

  protected def createClusters(locations: Seq[String]) = {
    locations.foldLeft(mutable.Map[String, Double]().withDefaultValue(0.0))({ (acc, nextLocation) =>
      acc(nextLocation) += 1.0
      acc
    })
  }

  def computeNeighborhoodSimilarityMatrix(brevisLocations: Seq[BrevisUserLocation]): Map[String, Seq[BrevisSimilarUser]] = {
    val t0 = System.currentTimeMillis()

    val locationsWithAddressComponents = brevisLocations.filter(bl => bl.location.nonEmpty)
    val locationsWithoutAddressComponents = brevisLocations.filter(bl => bl.location.isEmpty)

    Logger.warn(s"Locations without address components: ${locationsWithoutAddressComponents.map(l => l.id).mkString("\n")}")

    val userNeighborhoodsMap = locationsWithAddressComponents.foldLeft(mutable.Map[String, Seq[String]]().withDefaultValue(Seq[String]()))({ (acc, nextLocation) =>
      nextLocation.location.get.addressComponents.find(ac => ac.types.contains("neighborhood")) match {
        case Some(nl) =>
          acc(nextLocation.userId) = acc(nextLocation.userId) :+ nl.longName
          acc
        case None => acc
      }
    })

    val userNeighborhoodsList = userNeighborhoodsMap.toList
    val matrix = userNeighborhoodsList.foldLeft(Map[String, Seq[BrevisSimilarUser]]())({ (acc, uA) =>
      val similarUsersByNeighborhood = userNeighborhoodsList.filter(uB => uB._1 != uA._1) map { uB =>
        // using cosine similarity
        BrevisSimilarUser(uB._1, cosineSimilarity(uA._2, uB._2))

        // using sum
//        BrevisSimilarUser(uB._1, countSimilarity(uA._2, uB._2))
      }

      acc + (uA._1 -> similarUsersByNeighborhood)
    })

    val t1 = System.currentTimeMillis()
    println(s"computeSimilarityMatrix took: ${t1 - t0}ms")

    matrix
  }
}
