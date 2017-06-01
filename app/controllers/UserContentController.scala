package controllers

import com.google.inject.Inject
import io.swagger.annotations.Api
import models.BrevisUserContentMeta
import parsers.{CanParseBrevisBriefJSON, CanParseBrevisUserContentJSON, CanParseBrevisUserJSON}
import play.api.cache.{Cache, CacheApi, Cached}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{Controller, Result}
import play.api.{Configuration, Logger}
import play.mvc.Http.Response
import services.{BrevisBriefService, BrevisUserContentService}

import scala.concurrent.duration._

/**
  * Created by connor.jennings on 4/5/17.
  */

@Api(value = "/brevis/api/user/content")
class UserContentController @Inject() (
  auth: AuthenticateUser,
  brevisUserContentService: BrevisUserContentService,
  briefService: BrevisBriefService,
  configuration: Configuration,
  cacheAPI: CacheApi
) extends Controller with CanParseBrevisUserContentJSON
  with CanParseBrevisBriefJSON with CanParseBrevisUserJSON {

  implicit val brevisUserContentMetaUpdateReads: Reads[BrevisUserContentMetaUpdate] = (
      (JsPath \ "contentId").read[String] and
        (JsPath \ "meta").read[BrevisUserContentMeta]
  ) (BrevisUserContentMetaUpdate.apply _)

  case class BrevisUserContentMetaUpdate(
    contentId: String,
    meta: BrevisUserContentMeta
  )

  val imageDomain = "https://media.guim.co.uk"
  val reverseProxyImagePath = "/brevis/app/images"
  val imageRegex = "(https:\\/\\/media\\.guim\\.co\\.uk.+?)\""
  val imageMatcher = imageRegex.r

  def proxifyImages(str: String): String = {
    str
    .replaceAll(imageDomain, configuration.getString("brevis.host").get + reverseProxyImagePath)
    .replaceAll("1000.jpg", "500.jpg")

  }

  def content = auth.AuthenticatedUser { request =>
//    cacheAPI.getOrElse("user.content." + request.user.id, 0 seconds) {
      val contentsWithProxiedImages = brevisUserContentService.getContentForUser(request.user.id) map { userContent =>
        val content = userContent.content
        val maybeMatchedMainImage = imageMatcher.findAllIn(content.main)
        val maybeBodyImages = imageMatcher.findAllIn(content.bodyHtml)

        val matchedMainImage = {
          if (maybeMatchedMainImage.nonEmpty) {
            maybeMatchedMainImage.subgroups
          } else {
            Nil
          }
        }
        val bodyImages = {
          if (maybeBodyImages.nonEmpty) {
            maybeBodyImages.subgroups
          } else {
            Nil
          }
        }

        val proxifiedImages = (matchedMainImage ++ bodyImages).map(proxifyImages)

        (userContent.copy(
          content = content.copy(
            main = proxifyImages(content.main),
            bodyHtml = proxifyImages(content.bodyHtml),
            templatedHTML = Some(views.html.Article.article(content).body))),
          proxifiedImages)
      }

      val activeBrief = briefService.getActiveBrief()

      Logger.debug(s"Content request, user: ${request.user}")

      Ok(Json.toJson(Map(
        "user" -> Json.toJson(request.user),
        "brief" -> Json.toJson(activeBrief),
        "contents" -> Json.toJson(contentsWithProxiedImages.map(_._1)),
        "images" -> Json.toJson(contentsWithProxiedImages.map(_._2))
      )))
//    }
  }

  def updateMeta = auth.AuthenticatedUser(parse.json) { request =>
    request.body.validate[BrevisUserContentMetaUpdate] match {
      case json: JsSuccess[BrevisUserContentMetaUpdate] =>
        val metaUpdate = json.value

        val didDoMetaUpdate = brevisUserContentService.updateContentMeta(request.user.id, metaUpdate.contentId, metaUpdate.meta)

        Ok(Json.toJson(Map("success" -> didDoMetaUpdate)))
      case e: JsError =>
        Logger.error(e.toString)
        BadRequest(e.toString)
    }
  }
}
