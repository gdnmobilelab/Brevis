import db.UserPushSubscriptionDAO
import models.NewBriefNotification
import org.scalatest.{AsyncFlatSpec, Matchers}
import play.api.db.Databases
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.WsTestClient
import services.{FCMPushService, GuardianAPIService}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by connor.jennings on 4/20/17.
  */
class FCMPushServiceSpec extends AsyncFlatSpec with Matchers {

  "sendNotification" should "send a notification" in {
    WsTestClient.withClient { client =>
      val application = new GuiceApplicationBuilder().build()


      val apiSerivce = new GuardianAPIService(application.configuration)
      val database = Databases(
        "org.postgresql.Driver",
        "jdbc:postgresql://127.0.0.1/brevis",
        "brevis",
        Map(
          "user" -> "postgres"
        )
      )
      val result = Await.result(
        new FCMPushService(client, new UserPushSubscriptionDAO(database), application.configuration).sendNotification(NewBriefNotification(
          to = "c19dQahcAvo:APA91bGby1Hcvx6mhJrYwzm6raC-KE6DhG1m6Ht3H8trCpdEm1nzogBM9xx7XSdzaPoWxa4UiyghHVHNU__brtUV4OPiPQYjPN8mvNIU5_yu4x8IyIUykh45jYxpq87yuBGNJJndeZmC",
          title = "Test title",
          body = "Test body",
          url = "http://test"
        )), 10.seconds)

      assert(true === true)
    }
  }

//  "notifyNewBrief" should "send a new brief notification" in {
//    WsTestClient.withClient { client =>
//      val application = new GuiceApplicationBuilder().build()
//
//
//      val apiSerivce = new GuardianAPIService(application.configuration)
//      val database = Databases(
//        "org.postgresql.Driver",
//        "jdbc:postgresql://127.0.0.1/brevis",
//        "brevis",
//        Map(
//          "user" -> "postgres"
//        )
//      )
//      val result = Await.result(
//        new FCMPushService(client, new UserPushSubscriptionDAO(database), application.configuration).notifyNewBrief(), 10.seconds)
//
//      assert(true === true)
//    }
//  }

}
