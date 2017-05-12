package db

import anorm._
import anorm.SqlParser._
import com.google.inject.Inject
import db.dbparsers.CanParseBrevisPushSubscriptionResultSet
import models.{BrevisUserLocation, BrevisUserPushSubscription}
import play.api.db.Database
import play.api.libs.json.Json

/**
  * Created by connor.jennings on 4/20/17.
  */
class UserPushSubscriptionDAO @Inject()(db: Database) extends CanParseBrevisPushSubscriptionResultSet {
  def insertPushSubscription(userPushsubscription: BrevisUserPushSubscription): BrevisUserPushSubscription = {
    db.withConnection { implicit conn =>
      SQL("select * from p_InsertBrevisUserPushSubscription({userId}, {pushSubscriptionId}, {pushSubscriptionType})")
      .on('userId -> userPushsubscription.userId,
        'pushSubscriptionId -> userPushsubscription.pushSubscriptionId,
        'pushSubscriptionType -> userPushsubscription.pushSubscriptionType.toString
      )
      .as(brevisPushSubscriptionResultSetParser.single)
    }
  }

  def getUserPushSubscriptions(userId: String): Seq[BrevisUserPushSubscription] = {
    db.withConnection { implicit conn =>
      SQL("select * from p_GetBrevisUserPushSubscriptionsForUser({userId})")
      .on('userId -> userId)
      .as(brevisPushSubscriptionResultSetParser.*)
    }
  }

  def getAllPushSubscriptions(): Seq[BrevisUserPushSubscription] = {
    db.withConnection { implicit conn =>
      SQL("select * from p_GetBrevisUserPushSubscriptions()")
      .as(brevisPushSubscriptionResultSetParser.*)
    }
  }
}
