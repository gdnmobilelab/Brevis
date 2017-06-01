package controllers

import javax.inject.Inject

import io.swagger.annotations.Api
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.{FCMPushService, GuardianDailyEmailBriefCreator}
import util.CommuteRecommendationUtil

/**
  * Created by connor.jennings on 4/10/17.
  */
@Api(value = "/brevis/api/briefs")
class BrevisBriefController @Inject() (
  auth: AuthenticateUser,
  guardianDailyEmailBriefCreator: GuardianDailyEmailBriefCreator,
  commuteRecommendationUtil: CommuteRecommendationUtil,
  pushService: FCMPushService
) extends Controller {
  def createFromDailyEmail = Action {
    guardianDailyEmailBriefCreator.create()
    pushService.notifyNewBrief()
    Ok(Json.toJson(Map("created" -> true)))
  }
}
