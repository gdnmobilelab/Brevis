package parsers

import models.BrevisUserPushSubscriptionType.BrevisUserPushSubscriptionType
import models.dto.BrevisUserPushSubscriptionDTO
import models.{BrevisUserPushSubscription, BrevisUserPushSubscriptionType}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

/**
  * Created by connor.jennings on 4/20/17.
  */
trait CanParseBrevisUserPushSubscriptionJSONDTO {
  implicit val brevisPushSubscriptionTypeReads = Reads.enumNameReads(BrevisUserPushSubscriptionType)

  implicit val brevisPushSubscriptionDTOReads: Reads[BrevisUserPushSubscriptionDTO] = (
      (JsPath \ "pushSubscriptionId").read[String] and
      (JsPath \ "pushSubscriptionType").read[BrevisUserPushSubscriptionType]
  ) (BrevisUserPushSubscriptionDTO.apply _)


  implicit val brevisPushSubscriptionDTOWrites: Writes[BrevisUserPushSubscriptionDTO] = (
      (JsPath \ "pushSubscriptionId").write[String] and
      (JsPath \ "pushSubscriptionType").write[BrevisUserPushSubscriptionType]
  ) (unlift(BrevisUserPushSubscriptionDTO.unapply))
}
