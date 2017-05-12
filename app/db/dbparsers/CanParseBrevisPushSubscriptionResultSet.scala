package db.dbparsers

import anorm.SqlParser._
import anorm.~
import models._
import parsers.{CanParseBrevisAuthorJSON, CanParseBrevisContentTagJSON}
import play.api.libs.json.Json

/**
  * Created by connor.jennings on 4/20/17.
  */
trait CanParseBrevisPushSubscriptionResultSet {
  val brevisPushSubscriptionResultSetParser =
    int("brevis_user_push_subscription_id") ~
      str("brevis_user_push_subscription_user_id") ~
      str("brevis_user_push_subscription_push_id") ~
      str("brevis_user_push_subscription_type") map {
      case id ~ userId ~ pushId ~ pushType
      => BrevisUserPushSubscription(
        id = Some(id),
        userId = userId,
        pushSubscriptionId = pushId,
        pushSubscriptionType = BrevisUserPushSubscriptionType.withName(pushType)
      )
    }
}
