package services

import com.google.inject.Inject
import db.{UserContentDAO, UserContentRecommendationDAO}
import models.{BrevisContent, BrevisUserContent, BrevisUserContentMeta}

/**
  * Created by connor.jennings on 4/5/17.
  */
class BrevisUserContentService @Inject() (
  userContentDAO: UserContentDAO,
  userContentRecommendationDAO: UserContentRecommendationDAO
) {

  def updateContentMeta(userId: String, contentId: String, meta: BrevisUserContentMeta): Boolean = {
    userContentRecommendationDAO.updateContentMeta(userId, contentId, meta)
  }

  def getContentForUser(userId: String): Seq[BrevisUserContent] = {
    userContentRecommendationDAO.getRecommendedContentForUserId(userId)
  }

  def findRecentlyClickedContent(userId: String, limit: Int = 20): Seq[BrevisContent] = {
    userContentDAO.findUserRecentlyClickedContent(userId, limit)
  }
}
