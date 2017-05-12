package parsers

import models.{BrevisLocation, BrevisLocationAddressComponent, BrevisLocationGeometry, BrevisLocationLatLong}
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Reads, Writes}

/**
  * Created by connor.jennings on 3/24/17.
  */
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

trait CanParseLocationJSON {
  implicit val brevisLocationAddressComponentReads: Reads[BrevisLocationAddressComponent] = (
    (JsPath \ "long_name").read[String] and
      (JsPath \ "short_name").read[String] and
      (JsPath \ "types").read[Seq[String]]
    ) (BrevisLocationAddressComponent.apply _)

  implicit val brevisLocationReads: Reads[BrevisLocation] = (
    (JsPath \ "address_components").read[Seq[BrevisLocationAddressComponent]] and
      (JsPath \ "formatted_address").read[String] and
      ((
        (JsPath \ "geometry" \ "location" \ "lat").read[Double] and
          (JsPath \ "geometry" \ "location" \ "lng").read[Double]
        ) (BrevisLocationLatLong.apply _) and
        (JsPath \ "geometry" \ "location_type").read[String]
        ) (BrevisLocationGeometry.apply _) and
      (JsPath \ "place_id").read[String] and
      (JsPath \ "types").read[Seq[String]]
    )(BrevisLocation.apply _)

  implicit val brevisLocationAddressComponentWrites: Writes[BrevisLocationAddressComponent] = (
    (JsPath \ "long_name").write[String] and
      (JsPath \ "short_name").write[String] and
      (JsPath \ "types").write[Seq[String]]
    ) (unlift(BrevisLocationAddressComponent.unapply))

  implicit val brevisLocationWrites: Writes[BrevisLocation] = (
    (JsPath \ "address_components").write[Seq[BrevisLocationAddressComponent]] and
      (JsPath \ "formatted_address").write[String] and
      ((
        (JsPath \ "geometry" \ "location" \ "lat").write[Double] and
          (JsPath \ "geometry" \ "location" \ "lng").write[Double]
        ) (unlift(BrevisLocationLatLong.unapply)) and
        (JsPath \ "geometry" \ "location_type").write[String]
        ) (unlift(BrevisLocationGeometry.unapply)) and
      (JsPath \ "place_id").write[String] and
      (JsPath \ "types").write[Seq[String]]
    )(unlift(BrevisLocation.unapply))
}
