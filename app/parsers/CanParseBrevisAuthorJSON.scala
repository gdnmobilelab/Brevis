package parsers

import models.BrevisAuthor
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by connor.jennings on 3/29/17.
  */

trait CanParseBrevisAuthorJSON {
  implicit val brevisAuthorReads: Reads[BrevisAuthor] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "webUrl").read[String] and
      (JsPath \ "email").readNullable[String]
    ) (BrevisAuthor.apply _)

  implicit val brevisAuthorWrites: Writes[BrevisAuthor] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "name").write[String] and
      (JsPath \ "webUrl").write[String] and
      (JsPath \ "email").writeNullable[String]
    ) (unlift(BrevisAuthor.unapply))
}