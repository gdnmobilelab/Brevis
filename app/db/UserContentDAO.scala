package db

import anorm.SQL
import com.google.inject.Inject
import db.dbparsers.{CanParseBrevisContentResultSet, CanParseBrevisUserLocationResultSet}
import models.{BrevisContent, BrevisUserContentClick}
import parsers.CanParseLocationJSON
import play.api.Logger
import play.api.db.Database

/**
  * Created by connor.jennings on 4/18/17.
  */
class UserContentDAO @Inject()(db: Database)
  extends CanParseLocationJSON with CanParseBrevisUserLocationResultSet with CanParseBrevisContentResultSet {
  def insertUserContentClick(
    userId: String,
    contentId: String,
    locationId: Option[Int],
    dateISO: String,
    dateTimestamp: Long
  ): Boolean = {
    Logger.debug(
      s"""userId: ${userId}, contentId: ${contentId}
         | location: ${locationId}, dateISO: ${dateISO}
         | dateTimestamp: ${dateTimestamp}
       """.stripMargin)

    db.withConnection { implicit conn =>
      SQL("select * from p_InsertBrevisUserContentClick({userId}, {contentId}, {locationId}, {dateISO}, {dateTimestamp})")
      .on('userId -> userId,
        'contentId -> contentId,
        'locationId -> locationId,
        'dateISO -> dateISO,
        'dateTimestamp -> dateTimestamp
      )
      .execute()
    }
  }

  def findUserRecentlyClickedContent(userId: String, limit: Int): Seq[BrevisContent] = {
    db.withConnection { implicit conn =>
      SQL("select * from p_FindBrevisUserContentClick({userId}, {limit})")
      .on(
        'userId -> userId,
        'limit -> limit
      )
      .as(brevisContentResultSetParser.*)
    }
  }
}
