package parsers

import models.BrevisUser
import models.dto.{ContentDTO, LatLong, ContentReadObservationDTO}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by connor.jennings on 3/23/17.
  */


trait CanParseContentReadObservationDTOJSON extends CanParseLatLongJSON with CanParseContentDTOJSON {
  implicit val observationReadRequestReads: Reads[ContentReadObservationDTO] = (
      (JsPath \ "latlong").readNullable[LatLong] and
      (JsPath \ "content").read[ContentDTO] and
      (JsPath \ "dateISO").read[String]
  )(ContentReadObservationDTO.apply _)
}