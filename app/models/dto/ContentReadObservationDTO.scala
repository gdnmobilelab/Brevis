package models.dto

/**
  * Created by connor.jennings on 3/23/17.
  */
case class ContentReadObservationDTO (
  latlong: Option[LatLong],
  content: ContentDTO,
  dateISO: String
)
