package controllers

import com.google.inject.{Inject, Singleton}
import db.UserDAO
import models.BrevisUser
import parsers.CanParseBrevisUserJSON
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.Controller
import services.{BrevisBriefService, BrevisContentService, BrevisUserContentRecommendationCreator}
import util.{CommuteRecommendationUtil, CommuteType}

/**
  * Created by connor.jennings on 3/27/17.
  */

@Singleton
class UserController @Inject() (
  auth: AuthenticateUser,
  userDAO: UserDAO,
  contentService: BrevisContentService,
  briefService: BrevisBriefService,
  brevisUserContentRecommendationCreator: BrevisUserContentRecommendationCreator,
  commuteRecommendationUtil: CommuteRecommendationUtil,
  userContentRecommendationCreator: BrevisUserContentRecommendationCreator
) extends Controller with CanParseBrevisUserJSON {

  def getCurrentUser = auth.AuthenticatedUser { request =>
    Ok(Json.toJson(request.user))
  }

  def updateCurrentUser = auth.AuthenticatedUser(parse.json) { request =>
    request.body.validate[BrevisUser] match {
      case json: JsSuccess[BrevisUser] =>
        val brevisUser = json.value

        val updatedUser = userDAO.updateBrevisUser(request.user.copy(
          morningCommuteLength = brevisUser.morningCommuteLength,
          eveningCommuteLength = brevisUser.eveningCommuteLength,
          morningCommuteStart = brevisUser.morningCommuteStart,
          eveningCommuteStart = brevisUser.eveningCommuteStart
        ))

        Ok(Json.toJson(updatedUser))
      case e: JsError =>
        Logger.error(e.toString)
        BadRequest(e.toString)
    }
  }
}
