package parsers

import models.dto.LatLong
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._


/**
  * Created by connor.jennings on 3/31/17.
  */
trait CanParseLatLongJSON {
  implicit val latLongReads: Reads[LatLong] = (
    (JsPath \ "latitude").read[Double] and
      (JsPath \ "longitude").read[Double]
    )(LatLong.apply _)
}
