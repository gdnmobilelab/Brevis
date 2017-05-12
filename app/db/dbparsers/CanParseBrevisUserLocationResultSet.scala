package db.dbparsers

import anorm.SqlParser._
import anorm.~
import models.{BrevisLocation, BrevisUserLocation}
import parsers.CanParseLocationJSON
import play.api.libs.json.Json

/**
  * Created by connor.jennings on 4/4/17.
  */
trait CanParseBrevisUserLocationResultSet extends CanParseLocationJSON {
  val locationResultSetParser =
    int("brevis_user_location_id") ~
      str("brevis_user_location_userId") ~
      str("brevis_user_location_location") ~
      double("brevis_user_location_latitude") ~
      double("brevis_user_location_longitude") ~
      str("brevis_user_location_dateISO") ~
      int("brevis_user_location_dateTimestamp") map {
      case id ~ userId ~ location ~ latitude ~ longitude ~ dateISO ~ dateTimestamp =>
        BrevisUserLocation(
          id = Some(id),
          userId = userId,
          location = if (location.nonEmpty) { Some(Json.parse(location).as[BrevisLocation]) } else { None },
          latitude = latitude,
          longitude = longitude,
          dateISO = dateISO,
          dateTimestamp = dateTimestamp
        )
    }

  val maybeLocationResultParser =
    get[Option[Int]]("brevis_user_location_id") ~
      get[Option[String]]("brevis_user_location_userId") ~
      get[Option[String]]("brevis_user_location_location") ~
      get[Option[Double]]("brevis_user_location_latitude") ~
      get[Option[Double]]("brevis_user_location_longitude") ~
      get[Option[String]]("brevis_user_location_dateISO") ~
      get[Option[Int]]("brevis_user_location_dateTimestamp") map {
      case id ~ userId ~ location ~ latitude ~ longitude ~ dateISO ~ dateTimestamp =>
        id map { id =>
          BrevisUserLocation(
            id = Some(id),
            userId = userId.get,
            location = if (location.nonEmpty) { Some(Json.parse(location.get).as[BrevisLocation]) } else { None },
            latitude = latitude.get,
            longitude = longitude.get,
            dateISO = dateISO.get,
            dateTimestamp = dateTimestamp.get
          )
        }
    }
}
