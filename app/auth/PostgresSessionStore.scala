package auth

import javax.inject.Inject

import db.SessionDAO
import org.pac4j.core.context.Pac4jConstants
import org.pac4j.core.context.session.SessionStore
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore
import play.api.db.Database
import play.mvc.Http
import play.api.Logger

/**
  * Created by connor.jennings on 3/31/17.
  */
class PostgresSessionStore @Inject() (sessionDAO: SessionDAO) extends PlaySessionStore {

  val SEPARATOR = "$"
  val prefix = ""

  def getKey(sessionId: String, key: String): String = {
    prefix + SEPARATOR + sessionId + SEPARATOR + key
  }

  override def set(context: PlayWebContext, key: String, value: java.lang.Object): Unit = {
    val t1 = System.currentTimeMillis
    val sessionId = getOrCreateSessionId(context)

    val maxInactiveTime = {
      // Store a bunch of stuff in the session data for the auth flow
      // that's no longer needed once the user is authenticated
      // Don't keep it around so long
      if (key != "pac4jUserProfiles") {
        300
      } else {
        7776000
      }
    }

    sessionDAO.setSession(getKey(sessionId, key), value, maxInactiveTime)

    Logger.debug(s"Time to create user session: ${System.currentTimeMillis() - t1}ms")

  }

  override def destroySession(context: PlayWebContext): Boolean = {
    val session = context.getJavaSession
    val sessionId = session.get(Pac4jConstants.SESSION_ID)

    if (sessionId != null) {
      session.clear()
      return true
    }

    false
  }

  override def renewSession(context: PlayWebContext): Boolean = {
    false
  }

  override def getOrCreateSessionId(context: PlayWebContext): String = {
    val session = context.getJavaSession
    val cookieSessionId = session.get(Pac4jConstants.SESSION_ID)
    val headerSessionId = context.getRequestHeader("X-Brevis-Session")

    var sessionId = if (cookieSessionId != null) { cookieSessionId } else { headerSessionId }

    if (sessionId == null) {
      sessionId = java.util.UUID.randomUUID().toString
      session.put(Pac4jConstants.SESSION_ID, sessionId)
    }

    sessionId
  }

  override def get(context: PlayWebContext, key: String): Object = {
    val sessionId = getOrCreateSessionId(context)
    try { sessionDAO.getSession(getKey(sessionId, key)).sessionData } catch {
      case e: Exception =>
        Logger.debug(e.getMessage)
        null
    }
  }

  override def getTrackableSession(context: PlayWebContext): AnyRef = {
    context.getJavaSession.get(Pac4jConstants.SESSION_ID)
  }

  override def buildFromTrackableSession(context: PlayWebContext, trackableSession: scala.Any): SessionStore[PlayWebContext] = {
    context.getJavaSession.put(Pac4jConstants.SESSION_ID, trackableSession.asInstanceOf[String])
    this
  }
}
