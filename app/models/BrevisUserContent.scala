package models

/**
  * Created by connor.jennings on 4/10/17.
  */

case class BrevisUserContentMeta(
  read: Boolean,
  score: Double
)

case class BrevisUserContent (
  content: BrevisContent,
  meta: BrevisUserContentMeta
)
