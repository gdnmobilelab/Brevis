package util

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}

import com.google.inject.Inject
import models.BrevisContent
import recommenders.BrevisContentBasedRecommender
import util.CommuteType.CommuteType

import scala.util.Random

/**
  * Created by connor.jennings on 4/4/17.
  */

object CommuteType extends Enumeration {
  type CommuteType = Value
  val MORNING, EVENING = Value
}

class CommuteRecommendationUtil @Inject() (
  brevisContentBasedRecommender: BrevisContentBasedRecommender
) {
  val AVERAGE_USER_READING_TIME = 300 //words per minute

  def commuteTypeForTimezone(timezone: String = "America/New_York"): CommuteType = {
    if (ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("a")) == "AM") {
      CommuteType.MORNING
    } else {
      CommuteType.EVENING
    }
  }

  def recommendContentForUserCommute(
    commuteLength: Int,
    userContent: Seq[BrevisContent],
    newContent: Seq[BrevisContent]): Seq[(BrevisContent, Double)] = {
    val recommendedContent = brevisContentBasedRecommender.findMostSimilarContent(userContent, newContent)

    val commuteContent = pickContentForCommuteLength(
      commuteLength = commuteLength,
      contentList = brevisContentBasedRecommender.findMostSimilarContent(userContent, newContent).map(_._1))

    commuteContent.map((content) => {
      recommendedContent.find(c => c._1.id == content.id).get
    })
  }

  def pickContentForCommuteLength(
    commuteLength: Int,
    contentList: Seq[BrevisContent]): Seq[BrevisContent] = {

    val userReadingContent = contentList.foldLeft((Seq[BrevisContent](), 0)) { (listCount, content) =>
      val articleReadingTime = Math.ceil(content.wordCount.toDouble / AVERAGE_USER_READING_TIME.toDouble)

      if (listCount._2 > commuteLength) {
        listCount
      } else {
        listCount.copy(
          _1 = listCount._1 :+ content,
          _2 = listCount._2 + articleReadingTime.toInt
        )
      }
    }

    userReadingContent._1
  }
}
