package recommenders

import com.google.inject.Inject
import db.ContentDAO
import models.{BrevisContent, BrevisSimilarContent}

import scala.collection.immutable.HashSet
import scala.collection.mutable

/**
  * Created by connor.jennings on 3/22/17.
  */

class BrevisContentBasedRecommender {
  def findMostSimilarContent(
    userContent: Seq[BrevisContent],
    content: Seq[BrevisContent]): Seq[(BrevisContent, Double)] = {
    // Take userContent and reduce it to a set of features. Weights are the percentage of the total a feature appears

    val userFeaturesAndWeights = userContent.foldLeft(mutable.Map.empty[String, Double])((acc, content) => {
      val features = content.tags.map(t => t.id) ++ content.authors.map(a => a.id) ++ Set(content.sectionId)

      features foreach { feature =>
        acc.put(feature, acc.getOrElse(feature, 0.0) + 1.0)
      }

      acc
    })

    val userFeatures = userFeaturesAndWeights.keys
    val orderedSimilarContent = content
      .map((c) => {
        (c, cosineSimilarity(userFeatures.toSet, featuresFromContent(c).toSet, userFeaturesAndWeights.toMap))
      })
      .sortWith { (a, b) =>
        a._2 < b._2
      }

    orderedSimilarContent
  }

  private def featuresFromContent(content: BrevisContent): Seq[String] = {
    content.tags.map(t => t.id) ++ content.authors.map(a => a.id) ++ Seq(content.sectionId)
  }

  protected def cosineSimilarity(featuresA: Set[String], featuresB: Set[String], weights: Map[String, Double]): Double = {
    val combined = Set() ++ featuresA ++ featuresB

    val featuresVector = combined.foldLeft(List.empty[(Double, Double, Double)])((acc, feature) => {
      val featureA = if (featuresA.contains(feature)) { 1.0 } else { 0.0 }
      val featureB = if (featuresB.contains(feature)) { 1.0 } else { 0.0 }

      acc :+ (featureA, featureB, weights.getOrElse(feature, 1.0))
    })

    val numerator: Double = featuresVector.map((f) => f._1 * f._2 * f._3).sum
    val denominator: Double = Math.sqrt(featuresVector.map((f) => f._1 * f._1 * f._3).sum) * Math.sqrt(featuresVector.map((f) => f._2 * f._2 * f._2).sum)

    if (denominator == 0) { 0 } else { numerator / denominator }
  }

  // Naive cosine similarity computation
//  protected def cosineSimilarity(bcA: BrevisContent, bcB: BrevisContent): Double = {
//    // First, the tag
//    val tagsA = bcA.tags.map((tag) => tag.id)
//    val tagsB = bcB.tags.map((tag) => tag.id)
//
//    // The author
//    val authorsA = bcA.authors.map((author) => author.id)
//    val authorsB = bcB.authors.map((author) => author.id)
//
//    // The section
//    val sectionA = bcA.sectionId
//    val sectionB = bcB.sectionId
//
//    // Instead of iterating to check if a doc has a feature,
//    // make it constant time
//    val featuresA = HashSet() ++ tagsA ++ authorsA + sectionA
//    val featuresB = HashSet() ++ tagsB ++ authorsB + sectionB
//
//    // Combine them all
//    val combined = Set() ++ tagsA ++ tagsB ++ authorsA ++ authorsB + sectionA + sectionB
//
//    val docAVector = combined.foldLeft(List.empty[(Double, Double)])((acc, feature) => {
//      if (featuresA.contains(feature)) {
//        acc :+ (1.0, featureWeight(feature))
//      } else {
//        acc :+ (0.0, 0.0)
//      }
//    })
//
//    val docBVector = combined.foldLeft(List.empty[(Double, Double)])((acc, feature) => {
//      if (featuresB.contains(feature)) {
//        acc :+ (1.0, featureWeight(feature))
//      } else {
//        acc :+ (0.0, 0.0)
//      }
//    })
//
//    val numerator: Double = (docAVector zip docBVector).map((pair) => pair._1._2 * pair._2._1 * pair._1._2).sum
//    val denominator: Double = Math.sqrt(docAVector.map((a) => (a._1 * a._1) * a._2).sum) * Math.sqrt(docBVector.map((b) => (b._1 * b._1) * b._2).sum)
//
//    numerator / denominator
//  }

//  def computeSimilarityMatrix(brevisContents: Seq[BrevisContent]): Map[BrevisContent, Seq[BrevisSimilarContent]] = {
//    val t0 = System.nanoTime()
//
//    val matrix = brevisContents.foldLeft(Map[BrevisContent, Seq[BrevisSimilarContent]]())({ (acc, bcA: BrevisContent) =>
//      val brevisSimilarContent = brevisContents.filter(bc => bc.id != bcA.id) map { bcB: BrevisContent =>
//        BrevisSimilarContent(bcB.id, cosineSimilarity(bcA, bcB))
//      }
//
//      acc + (bcA -> brevisSimilarContent)
//    })
//
//    val t1 = System.nanoTime()
//    println(s"computeSimilarityMatrix took: ${t1 - t0}ns")
//
//    matrix
//  }
}
