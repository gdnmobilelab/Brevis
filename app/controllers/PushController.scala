package controllers

import com.google.inject.Inject
import db.UserPushSubscriptionDAO
import models.BrevisUserPushSubscription
import models.dto.BrevisUserPushSubscriptionDTO
import parsers.CanParseBrevisUserPushSubscriptionJSONDTO
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
  * Created by connor.jennings on 4/20/17.
  */
class PushController @Inject() (
  pushSubscriptionDAO: UserPushSubscriptionDAO,
  auth: AuthenticateUser
) extends Controller with CanParseBrevisUserPushSubscriptionJSONDTO {

  def create = auth.AuthenticatedUser.async(parse.json) { request =>
    request.body.validate[BrevisUserPushSubscriptionDTO] match {
      case json: JsSuccess[BrevisUserPushSubscriptionDTO] =>
        val pushSubscription = pushSubscriptionDAO.insertPushSubscription(BrevisUserPushSubscription(
          id = None,
          userId = request.user.id,
          pushSubscriptionId = json.value.pushSubscriptionId,
          pushSubscriptionType = json.value.pushSubscriptionType
        ))

        Future.successful(Ok(Json.toJson(Map("success" -> true))))
      case e: JsError =>
        Logger.error(e.toString)
        Future.successful(BadRequest(e.toString))
    }
  }
}
