package controllers

import java.util.UUID
import javax.inject._

import parsers.{CanParseBrevisContentJSON, CanParseBrevisUserJSON}
import play.api._
import play.api.mvc._
import services.{BrevisBriefService, BrevisContentService}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json


/**
  * Created by connor.jennings on 3/27/17.
  */

@Singleton
class BrevisAppController @Inject() (configuration: Configuration) (
  contentService: BrevisContentService,
  briefService: BrevisBriefService,
  auth: AuthenticateUser

) extends Controller with CanParseBrevisUserJSON {

  def login = Action.async { request =>
    Assets.at(path="/public/brevis/dist", file="html/login.html")(request)
  }

  def logout = Action.async { request =>
    Assets.at(path="/public/brevis/dist", file="html/logout.html")(request)
  }

  def index = auth.AuthenticatedUser.async { request =>
    Assets.at(path="/public/brevis/dist", file = "html/index.html")(request)
  }
}
