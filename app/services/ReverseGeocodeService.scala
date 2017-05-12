package services

import com.google.inject.Inject
import models.BrevisLocation
import parsers.CanParseLocationJSON
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsValue

import scala.concurrent.Future

/**
  * Created by connor.jennings on 3/23/17.
  */
class ReverseGeocodeService @Inject() (ws: WSClient, configuration: Configuration) extends CanParseLocationJSON {
  private val reverseGeocodeURL = (lat: Double, long: Double) => {
    s"https://maps.googleapis.com/maps/api/geocode/json?latlng=${lat},${long}&key=${configuration.getString("brevis.googleAPIKey").get}"
  }

  def reverseGeocodeResp(latitude: Double, longitude: Double): Future[BrevisLocation] = {
    val url = ws.url(reverseGeocodeURL(latitude, longitude))

    url.get().map(resp => {
      val results = (resp.json \ "results").as[List[JsValue]]
      results.head.as[BrevisLocation]
    })
  }
}
