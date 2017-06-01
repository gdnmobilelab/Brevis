package services

import com.google.inject.Inject
import db.UserPushSubscriptionDAO
import models.{NewBriefNotification, PushNotification}
import play.api.{Configuration, Logger}
import play.api.libs.json.{JsObject, JsString, Json}
import play.api.libs.ws.WSClient
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
  * Created by connor.jennings on 4/20/17.
  */
class FCMPushService @Inject() (
  ws: WSClient,
  userPushSubscriptionDAO: UserPushSubscriptionDAO,
  config: Configuration
) {
  val key = config.getString("brevis.fcm.pushKey").get

  def notifyNewBrief(): Future[Seq[Boolean]] = {
    Future.sequence(userPushSubscriptionDAO.getAllPushSubscriptions() map { subscription =>
      sendNotification(NewBriefNotification(
        to = subscription.pushSubscriptionId,
        title = "Howdy, you've got new content",
        body = "Enjoy!",
        url = s"${config.getString("brevis.host").get}/brevis/app/"
      ))
    })
  }

  def sendNotification(pushNotification: PushNotification): Future[Boolean] = {
    Logger.debug(s"Sending push notificaiton to: ${pushNotification.to}")
    ws.url("https://fcm.googleapis.com/fcm/send")
      .withHeaders(
        ("Authorization", s"key=$key"),
        ("Content-Type", "application/json")
      )
      .post(new JsObject(
        Map(
          "to" -> JsString(pushNotification.to),
          "data" -> Json.toJson(pushNotification.data)
        )
      ))
      .map((res) => {
        Logger.debug(res.json.toString())
        true
      })
  }
}
