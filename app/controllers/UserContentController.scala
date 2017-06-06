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

class UserContentController @Inject() (
  auth: AuthenticateUser,
  brevisUserContentService: BrevisUserContentService,
  configuration: Configuration,
  cacheAPI: CacheApi
) extends Controller with CanParseBrevisUserContentJSON
  with CanParseBrevisBriefJSON with CanParseBrevisUserJSON {


  def updateMeta(contentId: String) = auth.AuthenticatedUser(parse.json) { request =>
    request.body.validate[BrevisUserContentMeta] match {
      case json: JsSuccess[BrevisUserContentMeta] =>
        val metaUpdate = json.value

        val didDoMetaUpdate = brevisUserContentService.updateContentMeta(request.user.id, contentId, metaUpdate)

        Ok(Json.toJson(Map("success" -> didDoMetaUpdate)))
      case e: JsError =>
        Logger.error(e.toString)
        BadRequest(e.toString)
    }
  }

  def findClickedContent(limit: String) = auth.AuthenticatedUser { request =>
    val max = 100

    val intLimit = {
      val lim = try {
        limit.toInt
      } catch {
        case e: Exception => 20
      }

      if (lim > max) { max } else { lim }
    }

    Ok(Json.toJson(Map("content" -> brevisUserContentService.findClickedContent(request.user.id, intLimit))))
  }
}
