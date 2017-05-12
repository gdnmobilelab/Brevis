package parsers

import java.time.LocalTime

import models.BrevisUserAccountType.BrevisUserAccountType
import models.{BrevisContentTag, BrevisUser, BrevisUserAccountType}
import play.api.libs.json.{JsNull, _}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by connor.jennings on 4/3/17.
  */
trait CanParseBrevisUserJSON extends CanParseJava8TimeJSON {
  implicit val accountTypeReads = Reads.enumNameReads(BrevisUserAccountType)

  implicit val brevisUserReads: Reads[BrevisUser] = new Reads[BrevisUser] {
    override def reads(json: JsValue): JsResult[BrevisUser] = {
        val eveningCommuteStart = (json \ "eveningCommuteStart").getOrElse(JsNull) match {
          case s: JsString => Some(s.as[LocalTime])
          case _ => None
        }

        JsSuccess(BrevisUser(
          id = (json \ "id").as[String],
          externalId = (json \ "externalId").as[String],
          accountType = (json \ "accountType").as[BrevisUserAccountType],
          email = (json \ "email").asOpt[String],
          morningCommuteLength =  (json \ "morningCommuteLength").as[Int],
          eveningCommuteLength = (json \ "eveningCommuteLength").as[Int],
          morningCommuteStart = (json \ "morningCommuteStart").as[LocalTime],
          eveningCommuteStart = eveningCommuteStart
        ))
    }
  }

  implicit val brevisUserWrites: Writes[BrevisUser] = new Writes[BrevisUser] {
    override def writes(o: BrevisUser): JsValue = {
      JsObject(
        Map(
          "id" -> JsString(o.id),
          "externalId" -> JsString(o.externalId),
          "accountType" -> Json.toJson(o.accountType),
          "email" -> o.email.map(e => JsString(e)).getOrElse(JsNull),
          "morningCommuteLength" -> JsNumber(o.morningCommuteLength),
          "eveningCommuteLength" -> JsNumber(o.eveningCommuteLength),
          "morningCommuteStart" -> Json.toJson(o.morningCommuteStart),
          "eveningCommuteStart" -> Json.toJson(o.eveningCommuteStart)
        )
      )
    }
  }
}
