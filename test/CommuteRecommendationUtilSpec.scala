import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

import db.{BriefDAO, UserContentDAO, UserContentRecommendationDAO}
import models.BrevisBrief
import org.scalatest.{AsyncFlatSpec, Matchers}
import play.api.db.Databases
import play.api.inject.guice.GuiceApplicationBuilder
import recommenders.BrevisContentBasedRecommender
import services.{BrevisBriefService, BrevisUserContentService}
import util.CommuteRecommendationUtil

/**
  * Created by connor.jennings on 4/18/17.
  */
class CommuteRecommendationUtilSpec extends AsyncFlatSpec with Matchers {
  "recommendContentForUserCommute" should "recommend content for a user's commute" in {
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
    val userContentSerivce = new BrevisUserContentService(
      new UserContentDAO(database),
      new UserContentRecommendationDAO(database))
    val contentService = new BrevisUserContentService(
      new UserContentDAO(database),
      new UserContentRecommendationDAO(database))
    val commuteRecommendationUtil = new CommuteRecommendationUtil(new BrevisContentBasedRecommender())
    val recentlyClickedContent = userContentSerivce.findRecentlyClickedContent("756be903-eb42-4c16-80e4-5b08e6de2e13")

    val userReading = commuteRecommendationUtil.recommendContentForUserCommute(
      30,
      recentlyClickedContent,
      contentService.getContentForUser("756be903-eb42-4c16-80e4-5b08e6de2e13").map(buc => buc.content)
    )

    assert(userReading.nonEmpty)
  }
}
