package controllers

import javax.inject.Singleton

import play.api._
import play.api.libs.json.Json
import play.api.mvc._

/**
  * Created by connor.jennings on 3/27/17.
  */

@Singleton
class HealthCheckController extends Controller {
  def healthy = Action {
    Ok(Json.toJson(Map("healthy" -> true)))
  }
}
