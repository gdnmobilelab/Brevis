package models

/**
  * Created by connor.jennings on 4/4/17.
  */
case class BrevisUserContentRecommendation (
  id: Option[Int],
  userId: String,
  contentId: String,
  briefId: String,
  score: Double,
  active: Boolean
)
