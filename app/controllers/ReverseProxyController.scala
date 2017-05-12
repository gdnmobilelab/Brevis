package controllers

import com.google.inject.Inject
import play.api.libs.ws.{WSClient, WSResponseHeaders}
import play.api._
import play.api.http.HttpEntity
import play.api.mvc._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext


/**
  * Created by connor.jennings on 3/28/17.
  */
class ReverseProxyController @Inject() (WS: WSClient) extends Controller {
  def images(path: String) = Action.async { request =>

    val proxyRequest = WS.url("https://media.guim.co.uk/" + path)

    val cacheControl = "Cache-Control" -> "max-age=10800"

    // Todo: set our own cache-control "max-age" header for images?
    proxyRequest.stream.map {
      resp =>
        Result(
          ResponseHeader(resp.headers.status, resp.headers.headers.mapValues(_.head) + cacheControl),
          HttpEntity.Streamed.apply(resp.body, None, None))
    }
  }
}
