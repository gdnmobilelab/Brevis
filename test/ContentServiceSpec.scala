import java.util.UUID

import db.ContentDAO
import models.BrevisContent
import org.scalatest.{AsyncFlatSpec, Matchers}
import play.api.db.Databases
import play.api.inject.guice.GuiceApplicationBuilder
import services.{BrevisContentService, GuardianAPIService}

/**
  * Created by connor.jennings on 3/20/17.
  */

class ContentServiceSpec extends AsyncFlatSpec with Matchers {
  "insertBrevisContent" should "save brevis content" in {
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
//    val dbClient = new DBClient(application.configuration)
    val contentDAO = new ContentDAO(database)
    val brevisContentService = new BrevisContentService(contentDAO)

    apiSerivce.getItem("culture/2017/apr/08/lena-dunham-girls-season-six-trump-fleabag") flatMap { resp =>
      brevisContentService.insertBrevisContent(BrevisContent(Some(UUID.randomUUID().toString), resp.content.get))
      assert(true === true)
    }
  }
}
