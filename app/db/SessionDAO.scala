package db

import java.io.{ByteArrayInputStream, ObjectInputStream, ObjectOutputStream}
import java.time.Instant

import anorm.SQL
import com.google.inject.Inject
import play.api.db.Database
import anorm.SqlParser._
import anorm._
import auth.BrevisSession
import java.io.ByteArrayOutputStream
import java.io.ObjectOutput
import java.io.ObjectOutputStream

/**
  * Created by connor.jennings on 3/31/17.
  */
class SessionDAO @Inject()(db: Database) {
  val parser =
    str("brevis_session_id") ~
    long("brevis_session_creation_time") ~
    long("brevis_session_last_access_time") ~
    long("brevis_session_max_inactive_time") ~
    byteArray("brevis_session_data") map {
      case
        id ~ creationTime ~ lastAccessTime ~ maxInactiveTime ~ sessionData  =>
        BrevisSession(
          id = id,
          creationTime = creationTime,
          lastAccessTime = lastAccessTime,
          maxInactiveTime = maxInactiveTime,
          sessionData = new ObjectInputStream(new ByteArrayInputStream(sessionData)).readObject()
        )
    }


  def getSession(id: String): BrevisSession = {
    db.withConnection { implicit conn =>
      SQL("select * from p_GetBrevisSession({id})").on('id -> id).as(parser.single)
    }
  }

  def setSession(id: String, value: java.lang.Object, maxInactiveTime: Int) = {

    val bos = new ByteArrayOutputStream
    var out: ObjectOutput = null
    out = new ObjectOutputStream(bos)
    out.writeObject(value)
    out.flush()

    db.withConnection { implicit conn =>
      SQL(
        """
          select * from p_CreateBrevisSession(
          {id},
          {creationTime},
          {lastAccessTime},
          {maxInactiveTime},
          {sessionData})
        """).on(
        'id -> id,
        'creationTime -> Instant.now().getEpochSecond,
        'lastAccessTime -> Instant.now().getEpochSecond,
        'maxInactiveTime -> maxInactiveTime, // 90 days
        'sessionData -> bos.toByteArray
      ).execute()

    }
  }
}
