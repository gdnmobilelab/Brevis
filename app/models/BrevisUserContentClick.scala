package models

/**
  * Created by connor.jennings on 3/24/17.
  */
case class BrevisUserContentClick (
  id: Option[Int],
  userId: String,
  contentId: String,
  location: Option[BrevisUserLocation],
  dateISO: String,
  dateTimestamp: Long
)
