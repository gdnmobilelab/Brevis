package db

import java.sql.Date
import java.time.{Instant, LocalTime, ZoneId, ZonedDateTime}

import anorm._
import anorm.SqlParser._
import com.google.inject.Inject
import models.{BrevisUser, BrevisUserAccountType}
import play.api.db.Database
import DateTime._

/**
  * Created by connor.jennings on 3/31/17.
  */
class UserDAO @Inject() (db: Database) {
  val parser =
    str("brevis_user_userId") ~
      str("brevis_user_externalId") ~
      str("brevis_user_accountType") ~
      get[Option[String]]("brevis_user_email") ~
      int("brevis_morningCommuteLength") ~
      int("brevis_eveningCommuteLength") ~
      get[LocalTime]("brevis_morningCommuteStart") ~
      get[Option[LocalTime]]("brevis_eveningCommuteStart") map {
      case
        id ~ externalId ~ accountType ~ email ~ morningCommuteLength
          ~ eveningCommuteLength ~ morningCommuteStart ~ eveningCommuteStart =>
        BrevisUser(
          id = id,
          externalId = externalId,
          email = email,
          accountType = BrevisUserAccountType.withName(accountType),
          morningCommuteLength = morningCommuteLength,
          eveningCommuteLength = eveningCommuteLength,
          // Todo: implement timezones
          morningCommuteStart = morningCommuteStart,
          eveningCommuteStart = eveningCommuteStart
        )
    }

  def getUserByExternalId(externalId: String): Option[BrevisUser] = {
    db.withConnection { implicit conn =>
      SQL("select * from p_GetBrevisUserByExternalId({externalId})").on('externalId -> externalId).as(parser.singleOpt)
    }
  }

  def getUserById(userId: String): Option[BrevisUser] = {
    db.withConnection { implicit conn =>
      SQL("select * from p_GetBrevisUser({userId})").on('userId -> userId).as(parser.singleOpt)
    }
  }

  def createBrevisUser(brevisUser: BrevisUser): BrevisUser = {
    val eveningCommuteStart = if(brevisUser.eveningCommuteStart == null) { None } else { brevisUser.eveningCommuteStart }

    db.withConnection { implicit conn =>
      val sql = SQL(
        """
          select * from p_InsertBrevisUser(
          {id},
          {externalId},
          {email},
          {accountType},
          {morningCommuteLength},
          {eveningCommuteLength},
          {morningCommuteStart},
          {eveningCommuteStart})
        """).on(
        'id -> brevisUser.id,
        'externalId -> brevisUser.externalId,
        'email -> brevisUser.email,
        'accountType -> brevisUser.accountType.toString,
        'morningCommuteLength -> brevisUser.morningCommuteLength,
        'eveningCommuteLength -> brevisUser.eveningCommuteLength,
        'morningCommuteStart -> brevisUser.morningCommuteStart,
        'eveningCommuteStart -> eveningCommuteStart.orNull
      )

      sql.as(parser.single)
    }
  }

  def updateBrevisUser(brevisUser: BrevisUser): BrevisUser = {
    val eveningCommuteStart = if(brevisUser.eveningCommuteStart == null) { None } else { brevisUser.eveningCommuteStart }

    db.withConnection { implicit conn =>
      val sql = SQL(
        """
          select * from p_UpdateBrevisUser(
          {id},
          {morningCommuteLength},
          {eveningCommuteLength},
          {morningCommuteStart},
          {eveningCommuteStart})
        """).on(
        'id -> brevisUser.id,
        'morningCommuteLength -> brevisUser.morningCommuteLength,
        'eveningCommuteLength -> brevisUser.eveningCommuteLength,
        'morningCommuteStart -> brevisUser.morningCommuteStart,
        'eveningCommuteStart -> eveningCommuteStart.orNull
      )


      sql.as(parser.single)
    }
  }

  def getUsers(): Seq[BrevisUser] = {
    db.withConnection { implicit conn =>
      SQL("select * from p_GetBrevisUsers()").as(parser.*)
    }
  }
}
