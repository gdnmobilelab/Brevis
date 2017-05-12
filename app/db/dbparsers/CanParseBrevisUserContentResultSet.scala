package db.dbparsers

import anorm.SqlParser._
import anorm.~
import models.{BrevisUserContent, BrevisUserContentMeta}

/**
  * Created by connor.jennings on 4/11/17.
  */
trait CanParseBrevisUserContentResultSet extends CanParseBrevisContentResultSet {
  val brevisUserContentResultSetParser =
      bool("brevis_user_content_recommendation_read") ~
        double("brevis_user_content_recommendation_score") ~
      brevisContentResultSetParser map {
      case read ~ score ~ brevisContent
      => BrevisUserContent(
          content = brevisContent,
          meta = BrevisUserContentMeta(
            read = read,
            score = score
          )
      )
    }
}
