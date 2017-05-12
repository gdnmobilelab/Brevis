package models

import models.BrevisUserPushSubscriptionType.BrevisUserPushSubscriptionType

/**
  * Created by connor.jennings on 4/20/17.
  */

case class BrevisUserPushSubscription(
  id: Option[Int],
  userId: String,
  pushSubscriptionId: String,
  pushSubscriptionType: BrevisUserPushSubscriptionType
)

object BrevisUserPushSubscriptionType extends Enumeration {
  type BrevisUserPushSubscriptionType = Value
  val IOS, WEB = Value
}

