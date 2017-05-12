package db

import anorm.SqlParser._
import anorm._
import com.google.inject.Inject
import models.{BrevisBrief, BrevisContent}
import play.api.db.Database

/**
  * Created by connor.jennings on 3/27/17.
  */
class BriefDAO @Inject()(db: Database) {
  val parser =
    str("brevis_brief_id") ~
    str("brevis_brief_dateISO") ~
    int("brevis_brief_dateTimestamp") map {
      case id ~ dateISO ~ dateTimestamp =>
        BrevisBrief(
          id = id,
          dateISO = dateISO,
          dateTimestamp = dateTimestamp
        )
    }

  def getActiveBrief(): Option[BrevisBrief] = {
    db.withConnection { implicit conn =>
      SQL("select * from p_GetActiveBrief()").as(parser.singleOpt)
    }
  }

  def insertBrief(brevisBrief: BrevisBrief): BrevisBrief = {
    db.withConnection { implicit conn =>
      SQL("select * from p_InsertBrevisBrief({id}, {dateISO}, {dateTimestamp})")
        .on('id -> brevisBrief.id,
            'dateISO -> brevisBrief.dateISO,
            'dateTimestamp -> brevisBrief.dateTimestamp)
        .execute()

      brevisBrief
    }
  }

  def makeBriefActive(id: String): BrevisBrief = {
    db.withConnection { implicit conn =>
      SQL("select * from p_MakeBrevisBriefActive({id})")
      .on('id -> id)
      .as(parser.single)
    }
  }

  def addContentToBrief(contentId: String, briefId: String): Boolean = {
    db.withConnection { implicit conn =>
      SQL("select * from p_AddBrevisContentToBrief({contentId}, {briefId})")
      .on('contentId -> contentId,
          'briefId -> briefId)
      .execute()
    }
  }
}