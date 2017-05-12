package parsers

import models.dto.{LatLong, LocationObservationDTO}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by connor.jennings on 3/23/17.
  */
trait CanParseLocationObservationDTOJSON extends CanParseLatLongJSON {
  implicit val userLatLongDTOReads: Reads[LocationObservationDTO] = (
      (JsPath \ "latlong").read[LatLong] and
      (JsPath \ "dateISO").read[String]
    )(LocationObservationDTO.apply _)
}
