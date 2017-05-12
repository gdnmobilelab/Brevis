package models

/**
  * Created by connor.jennings on 3/24/17.
  */
case class BrevisLocationAddressComponent(
  longName: String,
  shortName: String,
  types: Seq[String]
)

case class BrevisLocationLatLong (
  lat: Double,
  lng: Double
)

case class BrevisLocationGeometry(
  location: BrevisLocationLatLong,
  locationType: String
)

case class BrevisLocation (
  addressComponents: Seq[BrevisLocationAddressComponent],
  formattedAddress: String,
  geometry: BrevisLocationGeometry,
  placeId: String,
  types: Seq[String]
)
