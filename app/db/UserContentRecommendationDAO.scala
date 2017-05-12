package db

import anorm._
import anorm.SqlParser._
import com.google.inject.Inject
import db.dbparsers.{CanParseBrevisContentResultSet, CanParseBrevisUserContentResultSet}
import models.{BrevisContent, BrevisUserContent, BrevisUserContentMeta, BrevisUserContentRecommendation}
import play.api.db.Database

/**
  * Created by connor.jennings on 4/4/17.
  */
class UserContentRecommendationDAO @Inject() (db: Database) extends CanParseBrevisUserContentResultSet {
  val parser =
    int("brevis_user_content_recommendation_id") ~
      str("brevis_user_content_recommendation_userId") ~
      str("brevis_user_content_recommendation_contentId") ~
      str("brevis_user_content_recommendation_briefId") ~
      double("brevis_user_content_recommendation_score") ~
      bool("brevis_user_content_recommendation_active") map {
      case id ~ userId ~ contentId ~ briefId ~ score ~ active =>
        BrevisUserContentRecommendation(
          id = Some(id),
          userId = userId,
          contentId = contentId,
          briefId = briefId,
          score = score,
          active = active
        )
    }

  def insertContentRecommendation(brevisUserContentRecommendation: BrevisUserContentRecommendation): BrevisUserContentRecommendation = {
    db.withConnection { implicit conn =>
      SQL("select * from p_InsertBrevisUserContentRecommendation({userId}, {contentId}, {briefId}, {score}, {active})")
      .on('userId -> brevisUserContentRecommendation.userId,
        'contentId -> brevisUserContentRecommendation.contentId,
        'briefId -> brevisUserContentRecommendation.briefId,
        'score -> brevisUserContentRecommendation.score,
        'active -> brevisUserContentRecommendation.active
      )
      .as(parser.single)
    }
  }

  def deleteContentRecommendationsForUser(userId: String): Seq[BrevisUserContentRecommendation] = {
    db.withConnection { implicit conn =>
      SQL("select * from p_DeleteBrevisContentRecommendationForUser({userId})")
        .on('userId -> userId)
        .as(parser.*)
    }
  }

  def updateContentMeta(userId: String, contentId: String, meta: BrevisUserContentMeta): Boolean = {
    db.withConnection { implicit conn =>
      SQL("select * from p_UpdateUserRecommendedContentMeta({userId}, {contentId}, {read})")
      .on(
        'userId -> userId,
        'contentId -> contentId,
        'read -> meta.read
      )
      .execute()
    }
  }

  def getRecommendedContentForUserId(userId: String): Seq[BrevisUserContent] = {
    db.withConnection { implicit conn =>
      SQL("select * from p_GetRecommendedContentForUser({userId})")
      .on('userId -> userId)
      .as(brevisUserContentResultSetParser.*)
    }
  }
}
