package parsers

import models.BrevisBrief
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._


/**
  * Created by connor.jennings on 4/10/17.
  */
trait CanParseBrevisBriefJSON {
  implicit val brevisBriefWrites: Writes[BrevisBrief] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "dateISO").write[String] and
      (JsPath \ "dateTimestamp").write[Long]
    ) (unlift(BrevisBrief.unapply))
}
