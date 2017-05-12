package parsers

import models.BrevisContent
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._


/**
  * Created by connor.jennings on 4/5/17.
  */
trait CanParseBrevisContentJSON extends CanParseBrevisAuthorJSON {
  implicit val brevisContentWrites: Writes[BrevisContent] = new Writes[BrevisContent] {
    override def writes(o: BrevisContent): JsValue = {
      Json.toJson(
        Map(
          "id" -> JsString(o.id.get),
          "headline" -> JsString(o.headline),
          "standfirst" -> JsString(o.standfirst),
          "byline" -> JsString(o.byline),
          "main" -> JsString(o.main),
          "bodyHtml" -> JsString(o.bodyHtml),
          "byline" -> JsString(o.byline),
          "webPublicationDateISO" -> JsString(o.webPublicationDateISO),
          "webPublicationDateTimestamp" -> JsNumber(o.webPublicationDateTimestamp)
        )
      )
    }
  }
}
