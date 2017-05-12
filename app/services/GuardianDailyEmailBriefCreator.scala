package services

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZonedDateTime}
import java.util.UUID

import com.google.inject.Inject
import com.gu.contentapi.client.model.v1.ItemResponse
import models.{BrevisBrief, BrevisContent, BrevisUserContentRecommendation}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import recommenders.BrevisContentBasedRecommender
import util.{CommuteRecommendationUtil, CommuteType}
import util.CommuteType.CommuteType

import scala.concurrent.Future
import scala.util.{Random, Success, Try}

/**
  * Created by connor.jennings on 4/10/17.
  */

class GuardianDailyEmailBriefCreator @Inject() (
  scraper: GuardianDailyScraper,
  apiService: GuardianAPIService,
  contentService: BrevisContentService,
  briefService: BrevisBriefService,
  recommenderService: BrevisContentBasedRecommender,
  userService: BrevisUserService,
  userContentService: BrevisUserContentService,
  userContentRecommendationService: BrevisUserContentRecommendationCreator,
  commuteRecommendationUtil: CommuteRecommendationUtil
) {
  def create() = {
    val commuteType = commuteRecommendationUtil.commuteTypeForTimezone()
    val articleIds = scraper.retrieveArticleIds()

    // Future.sequence executes in parallel, but that triggers the content api's rate limit
    // Executing the requests sequentially instead
    val articlesRequest: Future[Set[(ItemResponse, String)]] = articleIds.foldLeft(Future.successful(Set[(ItemResponse, String)]()))((accl, next) => {
      accl flatMap { list =>
        apiService.getItem(next) map { article =>
          list ++ Set((article, next))
        } recover {
          case e: Exception =>
            Logger.error(s"Failed to fetch: ${next}")
            list
        }
      }
    })

    // Usually fails if we trigger the content api's rate limit
    articlesRequest onFailure {
      case e =>
        Logger.error(e.getMessage)
    }

    articlesRequest foreach { articles =>
      val newBrief = BrevisBrief(UUID.randomUUID().toString, ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), Instant.now().getEpochSecond)

      val articlesWithoutContent = articles filter { article => article._1.content.isEmpty }
      val articlesWithContent = articles filter { article => article._1.content.isDefined }
      val tryBrevisContents = articlesWithContent map { article => Try(BrevisContent(article._1.content.get)) }

      val brevisContents = tryBrevisContents collect { case Success(s) => s }

      /** Log all the articles we failed to scrape **/
      tryBrevisContents.zip(articlesWithContent) foreach { content =>
        if(content._1.isFailure) {
          Logger.error(s"Failed to transform Guardian article into Brevis article: ${content._2._2}")
          Logger.error(content._1.failed.get.getMessage)
        }
      }

      // Create a new brief from the scraped content
      briefService.insertBrevisBrief(brevisBrief = newBrief)
      val createdContents = brevisContents map { content =>
        // Todo: eventually consolidate this so it's not two calls for every article
        val newContent = contentService.insertBrevisContent(content)
        briefService.addContentToBrief(newContent.id.get, newBrief.id)
        newContent
      }

      // For each user, create their commute list
      userService.getUsers() foreach { user =>
        Logger.debug(s"Creating content recommendations for user ${user.id}")
        val commuteLength = commuteType match {
          case CommuteType.MORNING => user.morningCommuteLength
          case CommuteType.EVENING => user.eveningCommuteLength
        }


        val recommendedForBrief = userContentRecommendationService.createScoredContentForBrief(
          userId = user.id,
          briefId = newBrief.id,
          commuteLength = commuteLength,
          newContent = createdContents.toSeq
        )

        userContentRecommendationService.deleteRecommendationsForUser(user.id)
        userContentRecommendationService.saveRecommendedContentToUser(recommendedForBrief, user)
      }

      briefService.makeBriefActive(newBrief.id)

      if (articlesWithoutContent.nonEmpty) {
        Logger.error(s"Articles without content: ${articlesWithoutContent.map(article => article._2).mkString("\n")} ")
      }

      Logger.debug(s"Articles with content: ${articlesWithContent.map(article => article._2).mkString("\n")}")
    }
  }
}
