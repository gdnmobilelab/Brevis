package services

import com.google.inject.Inject
import db.{UserContentDAO, UserLocationDAO}
import models.{BrevisUserContentClick, BrevisUserLocation}

/**
  * Created by connor.jennings on 3/23/17.
  */
class BrevisObservationService @Inject() (
  userLocationDAO: UserLocationDAO,
  userContentDAO: UserContentDAO,
  reverseGeocodeService: ReverseGeocodeService
){

  def saveUserLocation(brevisUserLocation: BrevisUserLocation): BrevisUserLocation = {
    userLocationDAO.insertUserLocation(brevisUserLocation)
  }

  def saveUserContentClick(
    userId: String,
    contentId: String,
    locationId: Option[Int],
    dateISO: String,
    dateTimestamp: Long): Boolean = {
    userContentDAO.insertUserContentClick(
      userId = userId,
      contentId = contentId,
      locationId = locationId,
      dateISO = dateISO,
      dateTimestamp = dateTimestamp
    )
  }
}
