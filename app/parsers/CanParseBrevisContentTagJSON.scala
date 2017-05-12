package parsers

import models.BrevisContentTag
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by connor.jennings on 3/29/17.
  */

trait CanParseBrevisContentTagJSON {
  implicit val brevisContentTagReads: Reads[BrevisContentTag] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "tagType").read[String] and
      (JsPath \ "sectionId").read[String] and
      (JsPath \ "sectionName").read[String] and
      (JsPath \ "webTitle").read[String] and
      (JsPath \ "webUrl").read[String]
    ) (BrevisContentTag.apply _)

  implicit val brevisContentTagWrites: Writes[BrevisContentTag] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "tagType").write[String] and
      (JsPath \ "sectionId").write[String] and
      (JsPath \ "sectionName").write[String] and
      (JsPath \ "webTitle").write[String] and
      (JsPath \ "webUrl").write[String]
    ) (unlift(BrevisContentTag.unapply))
}
