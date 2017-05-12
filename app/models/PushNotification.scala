package models

import play.api.libs.json.{JsObject, JsString, JsValue, Json}


/**
  * Created by connor.jennings on 4/20/17.
  */
case class PushNotification (
  to: String,
  data: JsValue
)

object NewBriefNotification {
  def apply(to: String,
            title: String,
            body: String,
            url: String): PushNotification = {
    PushNotification(
      to = to,
      data = new JsObject(
        Map(
          "type" -> JsString("NEW_BRIEF"),
          "title" -> JsString(title),
          "payload" -> new JsObject(
            Map(
              "icon" -> JsString("https://www.gdnmobilelab.com/images/logo-192.png"),
              "body" -> JsString(body),
              "data" -> new JsObject(
                Map(
                  "onClick" -> JsString(url)
                )
              )
            )
          )
        )
      )
    )
  }
}