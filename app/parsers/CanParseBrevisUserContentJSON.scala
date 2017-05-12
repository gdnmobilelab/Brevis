package parsers

import models.{BrevisContent, BrevisUserContent, BrevisUserContentMeta}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by connor.jennings on 4/11/17.
  */
trait CanParseBrevisUserContentJSON extends CanParseBrevisContentJSON {
  implicit val brevisUsercontentMetaWrites: Writes[BrevisUserContentMeta] = new Writes[BrevisUserContentMeta] {
    override def writes(o: BrevisUserContentMeta): JsValue = {
      JsObject(Map(
        "read" -> JsBoolean(o.read),
        "score" -> JsNumber(o.score)
      ))
    }
  }

  implicit val brevisUserContentMetaReads: Reads[BrevisUserContentMeta] = new Reads[BrevisUserContentMeta] {
    override def reads(json: JsValue): JsResult[BrevisUserContentMeta] = {
      JsSuccess(
        BrevisUserContentMeta(
          read = (json \ "read").as[Boolean],
          score = (json \ "score").as[Double]
        )
      )
    }
  }

  implicit val brevisUserContentWrites: Writes[BrevisUserContent] = (
    (JsPath \ "content").write[BrevisContent] and
      (JsPath \ "meta").write[BrevisUserContentMeta]
  ) (unlift(BrevisUserContent.unapply))
}
