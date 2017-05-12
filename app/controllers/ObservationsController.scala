package controllers

import java.time.Instant
import java.time.format.DateTimeFormatter

import com.google.inject.Inject
import models.dto.{ContentReadObservationDTO, LocationObservationDTO}
import models.{BrevisUserContentClick, BrevisUserLocation}
import parsers.{CanParseContentReadObservationDTOJSON, CanParseLocationObservationDTOJSON}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import services.{BrevisObservationService, ReverseGeocodeService}

import scala.concurrent.Future

/**
  * Created by connor.jennings on 3/23/17.
  */

class ObservationsController @Inject() (
  brevisObservationService: BrevisObservationService,
  reverseGeocodeService: ReverseGeocodeService,
  auth: AuthenticateUser
) extends Controller with CanParseContentReadObservationDTOJSON with CanParseLocationObservationDTOJSON {
  val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  def read = auth.AuthenticatedUser.async(parse.json) { request =>
    request.body.validate[ContentReadObservationDTO] match {
      case json: JsSuccess[ContentReadObservationDTO] =>
        val userContentClick = json.value

        Logger.debug(s"Read observation has latlong: ${userContentClick.latlong}")

        userContentClick.latlong match {
          // If we managed to get a location
          case Some(latlong) =>

            // Try fetching location details from Google
            reverseGeocodeService.reverseGeocodeResp(latlong.latitude, latlong.longitude) map { brevisLocation =>
              val savedLocation = brevisObservationService.saveUserLocation(BrevisUserLocation(
                id = None,
                userId = request.user.id,
                location = Some(brevisLocation),
                latitude = latlong.latitude,
                longitude = latlong.longitude,
                dateISO = userContentClick.dateISO,
                dateTimestamp = Instant.from(formatter.parse(userContentClick.dateISO)).getEpochSecond
              ))

              brevisObservationService.saveUserContentClick(
                userId = request.user.id,
                contentId = userContentClick.content.id,
                locationId = savedLocation.id,
                dateISO = userContentClick.dateISO,
                dateTimestamp = Instant.from(formatter.parse(userContentClick.dateISO)).getEpochSecond
              )

              Ok(Json.toJson(Map("savedClick" -> true, "savedLocation" -> true)))
            } recover { case e: Exception => // Failed to fetch location details from google
              Logger.error(e.getMessage)

              val savedLocation = brevisObservationService.saveUserLocation(BrevisUserLocation(
                id = None,
                userId = request.user.id,
                location = None,
                latitude = latlong.latitude,
                longitude = latlong.longitude,
                dateISO = userContentClick.dateISO,
                dateTimestamp = Instant.from(formatter.parse(userContentClick.dateISO)).getEpochSecond
              ))

              brevisObservationService.saveUserContentClick(
                userId = request.user.id,
                contentId = userContentClick.content.id,
                locationId =  savedLocation.id,
                dateISO = userContentClick.dateISO,
                dateTimestamp = Instant.from(formatter.parse(userContentClick.dateISO)).getEpochSecond
              )

              Ok(Json.toJson(Map("savedClick" -> true, "savedLocation" -> true)))
            }
          case None => // Failed to fetch user's location
            brevisObservationService.saveUserContentClick(
              userId = request.user.id,
              contentId = userContentClick.content.id,
              locationId = None,
              dateISO = userContentClick.dateISO,
              dateTimestamp = Instant.from(formatter.parse(userContentClick.dateISO)).getEpochSecond
            )

            Future.successful(Ok(Json.toJson(Map("savedClick" -> true))))
        }
      case e: JsError =>
        Future.successful(BadRequest(Json.toJson(Map("errorValidation" -> true))))
    }
  }

  def location = auth.AuthenticatedUser.async(parse.json) { request =>
    request.body.validate[LocationObservationDTO] match {
      case s: JsSuccess[LocationObservationDTO] =>
        val userLocation = s.value
        val latlong = userLocation.latlong

        // Try fetching details from google
        reverseGeocodeService.reverseGeocodeResp(latlong.latitude, latlong.longitude) map { brevisLocation =>
          brevisObservationService.saveUserLocation(BrevisUserLocation(
            id = None,
            userId = request.user.id,
            location = Some(brevisLocation),
            latitude = latlong.latitude,
            longitude = latlong.longitude,
            dateISO = userLocation.dateISO,
            dateTimestamp = Instant.from(formatter.parse(userLocation.dateISO)).getEpochSecond
          ))
        } map { brevisUserLocation =>
          Ok(Json.toJson(Map("savedLocation" -> true)))
        } recover { case e: Exception => // Failed to fetch details from Google
          Logger.error(e.getMessage)

          brevisObservationService.saveUserLocation(BrevisUserLocation(
            id = None,
            userId = request.user.id,
            location = None,
            latitude = latlong.latitude,
            longitude = latlong.longitude,
            dateISO = userLocation.dateISO,
            dateTimestamp = Instant.from(formatter.parse(userLocation.dateISO)).getEpochSecond
          ))

          Ok(Json.toJson(Map("savedLocation" -> true)))
        }
      case e: JsError =>
        Future.successful(BadRequest(Json.toJson(Map("errorValidation" -> true))))
    }
  }
}
