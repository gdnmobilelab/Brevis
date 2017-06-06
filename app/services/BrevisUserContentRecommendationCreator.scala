package services

import com.google.inject.Inject
import db.{ContentDAO, UserContentRecommendationDAO, UserDAO}
import models._
import util.CommuteRecommendationUtil

/**
  * Created by connor.jennings on 4/3/17.
  */
class BrevisUserContentRecommendationCreator @Inject() (
  userContentRecommendationDAO: UserContentRecommendationDAO,
  userContentService: BrevisUserContentService,
  commuteRecommendationUtil: CommuteRecommendationUtil
) {

  def createScoredContentForBrief(
    userId: String,
    briefId: String,
    commuteLength: Int,
    newContent: Seq[BrevisContent]): Seq[BrevisUserContentRecommendation] = {
    val recentlyClickedContent = userContentService.findClickedContent(userId)

    val userReading = commuteRecommendationUtil.recommendContentForUserCommute(
      commuteLength,
      recentlyClickedContent,
      newContent
    )

    userReading map { contentScore =>
      BrevisUserContentRecommendation(
        id = None,
        userId = userId,
        contentId = contentScore._1.id.get,
        briefId = briefId,
        score = contentScore._2,
        active = true
      )
    }
  }

  def deleteRecommendationsForUser(userId: String): Seq[BrevisUserContentRecommendation] = {
    userContentRecommendationDAO.deleteContentRecommendationsForUser(userId)
  }

  def saveRecommendedContentToUser(
    brevisUserContentRecommendation: Seq[BrevisUserContentRecommendation],
    brevisUser: BrevisUser): Seq[BrevisUserContentRecommendation] = {
    // Delete the current recommendations for the user
    // Add the new recommendations for the user
    brevisUserContentRecommendation map { content =>
      userContentRecommendationDAO.insertContentRecommendation(content)
    }
  }

  def getContentRecommendationsForUser(userId: String): Seq[BrevisUserContent] = {
    userContentRecommendationDAO.getRecommendedContentForUserId(userId)
  }
}
