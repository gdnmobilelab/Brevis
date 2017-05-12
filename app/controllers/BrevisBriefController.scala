package controllers

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.Controller
import services.{FCMPushService, GuardianDailyEmailBriefCreator}
import util.CommuteRecommendationUtil

/**
  * Created by connor.jennings on 4/10/17.
  */
class BrevisBriefController @Inject() (
  auth: AuthenticateUser,
  guardianDailyEmailBriefCreator: GuardianDailyEmailBriefCreator,
  commuteRecommendationUtil: CommuteRecommendationUtil,
  pushService: FCMPushService
) extends Controller {
  def createFromDailyEmail = auth.AuthenticatedUser {
    guardianDailyEmailBriefCreator.create()
    pushService.notifyNewBrief()
    Ok(Json.toJson(Map("created" -> true)))
  }
}
