package db

import anorm.SqlParser._
import anorm.{SQL, ~}
import com.google.inject.Inject
import db.dbparsers.CanParseBrevisUserLocationResultSet
import models._
import parsers.CanParseLocationJSON
import play.api.db.Database
import play.api.libs.json.Json

/**
  * Created by connor.jennings on 3/23/17.
  */
class UserLocationDAO @Inject()(db: Database) extends CanParseBrevisUserLocationResultSet {

  def insertUserLocation(brevisUserLocation: BrevisUserLocation): BrevisUserLocation = {
    db.withConnection { implicit conn =>
      SQL("select * from p_InsertBrevisUserLocation({userId}, {location}, {latitude}, {longitude}, {dateISO}, {dateTimestamp})")
      .on('userId -> brevisUserLocation.userId,
        'location -> brevisUserLocation.location.map(location => Json.toJson(location).toString).orNull,
        'latitude -> brevisUserLocation.latitude,
        'longitude -> brevisUserLocation.longitude,
        'dateISO -> brevisUserLocation.dateISO,
        'dateTimestamp -> brevisUserLocation.dateTimestamp
      )
      .as(locationResultSetParser.single)
    }
  }
}
