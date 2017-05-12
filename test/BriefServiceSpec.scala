import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter
import java.util.UUID

import db.{BriefDAO, ContentDAO, UserContentDAO, UserContentRecommendationDAO}
import models.{BrevisBrief, BrevisContent}
import org.scalatest.{AsyncFlatSpec, FlatSpec, Matchers}
import play.api.db.Databases
import play.api.inject.guice.GuiceApplicationBuilder
import services.{BrevisBriefService, BrevisContentService, BrevisUserContentService, GuardianAPIService}

/**
  * Created by connor.jennings on 3/29/17.
  */
class BriefServiceSpec extends AsyncFlatSpec with Matchers {
  "makeBriefActive" should "make the brief active" in {
    val application = new GuiceApplicationBuilder().build()

    val database = Databases(
      "org.postgresql.Driver",
      "jdbc:postgresql://127.0.0.1/brevis",
      "brevis",
      Map(
        "user" -> "postgres"
      )
    )
    //    val dbClient = new DBClient(application.configuration)
    val briefDAO = new BriefDAO(database)
    val brevisBriefService = new BrevisBriefService(briefDAO)
    val date = LocalDateTime.now()
    val briefId = UUID.randomUUID().toString

    brevisBriefService.insertBrevisBrief(BrevisBrief(
      id = briefId,
      dateISO = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
      dateTimestamp = date.toEpochSecond(ZoneOffset.UTC)
    ))

    assert(brevisBriefService.makeBriefActive(briefId).id === briefId)
  }

  "addContentToBrief" should "add content to a brief" in {
    val application = new GuiceApplicationBuilder().build()

    val database = Databases(
      "org.postgresql.Driver",
      "jdbc:postgresql://127.0.0.1/brevis",
      "brevis",
      Map(
        "user" -> "postgres"
      )
    )
    //    val dbClient = new DBClient(application.configuration)
    val briefDAO = new BriefDAO(database)
    val brevisBriefService = new BrevisBriefService(briefDAO)
    val date = LocalDateTime.now()
    val briefId = UUID.randomUUID().toString

    brevisBriefService.insertBrevisBrief(BrevisBrief(
      id = briefId,
      dateISO = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
      dateTimestamp = date.toEpochSecond(ZoneOffset.UTC)
    ))

    val apiSerivce = new GuardianAPIService(application.configuration)
    val contentDAO = new ContentDAO(database)
    val brevisContentService = new BrevisContentService(contentDAO)

    apiSerivce.getItem("commentisfree/2017/mar/17/meals-on-wheels-fatality-trumps-budget") flatMap { resp =>
      val contentId = UUID.randomUUID().toString
      brevisContentService.insertBrevisContent(BrevisContent(Some(contentId), resp.content.get))
      assert(brevisBriefService.addContentToBrief(contentId, briefId) === true)
    }
  }
}