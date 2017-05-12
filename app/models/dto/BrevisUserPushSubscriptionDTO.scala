package models.dto

import models.BrevisUserPushSubscriptionType.BrevisUserPushSubscriptionType

/**
  * Created by connor.jennings on 4/20/17.
  */
case class BrevisUserPushSubscriptionDTO (
  pushSubscriptionId: String,
  pushSubscriptionType: BrevisUserPushSubscriptionType
)
