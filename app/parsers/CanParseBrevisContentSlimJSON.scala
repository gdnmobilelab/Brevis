package parsers

import models.BrevisContent
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by connor.jennings on 4/10/17.
  */
trait CanParseBrevisContentSlimJSON {
  implicit val brevisSlimContentWrites: Writes[BrevisContent] = new Writes[BrevisContent] {
    override def writes(o: BrevisContent): JsValue = {
      Json.toJson(
        Map(
          "id" -> o.id.get,
          "headline" -> o.headline,
          "standfirst" -> o.standfirst
        )
      )
    }
  }
}
