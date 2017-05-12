package controllers

import javax.inject.Inject

import db.UserDAO
import models.BrevisUser
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore
import play.api.libs.json.Json
import play.api.mvc.Results.Unauthorized
import play.api.mvc.Security.{AuthenticatedBuilder, AuthenticatedRequest}
import play.api.mvc._

import play.api.Logger

import scala.concurrent.Future

/**
  * Created by connor.jennings on 3/31/17.
  */

class BrevisUserRequest[A](val user: BrevisUser, request: Request[A]) extends WrappedRequest[A](request)

// Todo: should authenticate against DB here
class AuthenticateUser @Inject() (
  playSessionStore: PlaySessionStore,
  userDAO: UserDAO
) {
  object AuthenticatedUser extends ActionBuilder[BrevisUserRequest] {
    def invokeBlock[A](request: Request[A], block: (BrevisUserRequest[A]) => Future[Result]) = {
      val webContext = new PlayWebContext(request, playSessionStore)
      val profileManager = new ProfileManager[CommonProfile](webContext)
      val maybeProfile = profileManager.get(true)

      if (maybeProfile.isPresent) {
        val profile = maybeProfile.get()

        val ts1 = System.currentTimeMillis()

        val brevisUser = userDAO.getUserByExternalId(profile.getTypedId)

        Logger.debug(s"Time to fetch user: ${System.currentTimeMillis() - ts1}ms")

        if (brevisUser.isDefined) {
          block(new BrevisUserRequest[A](brevisUser.get, request))
        } else {
          Future.successful(Unauthorized(Json.toJson(Map("userExists" -> false))))
        }
      } else {
        Future.successful(Unauthorized(Json.toJson(Map("userExists" -> false))))
      }
    }
  }
}