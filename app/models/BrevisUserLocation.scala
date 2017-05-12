package models

/**
  * Created by connor.jennings on 3/24/17.
  */
case class BrevisUserLocation (
  id: Option[Int],
  userId: String,
  location: Option[BrevisLocation],
  latitude: Double,
  longitude: Double,
  dateISO: String,
  dateTimestamp: Long
)
