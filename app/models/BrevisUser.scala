package models

import java.time.{Instant, LocalTime, ZonedDateTime}

import models.BrevisUserAccountType.BrevisUserAccountType

/**
  * Created by connor.jennings on 3/23/17.
  */

object BrevisUserAccountType extends Enumeration {
  type BrevisUserAccountType = Value
  val GOOGLE = Value
}

case class BrevisUser(
  id: String,
  externalId: String,
  accountType: BrevisUserAccountType,
  email: Option[String],
  firstName: String,
  lastName: String,
  morningCommuteLength: Int,
  eveningCommuteLength: Int,
  morningCommuteStart: LocalTime,
  eveningCommuteStart: Option[LocalTime]
)

object BrevisUser {
  // Exists for java-interop
  def apply(id: String, externalId: String, accountType: BrevisUserAccountType,
    email: String, firstName: String, lastName: String, morningCommuteLength: Int, eveningCommuteLength: Int,
    morningCommuteStart: LocalTime): BrevisUser = {
      BrevisUser(
        id = id,
        externalId = externalId,
        accountType = accountType,
        email = if (email.isEmpty) { None } else { Some(email) },
        firstName = firstName,
        lastName = lastName,
        morningCommuteLength = morningCommuteLength,
        eveningCommuteLength = eveningCommuteLength,
        morningCommuteStart = morningCommuteStart,
        eveningCommuteStart = None
      )
  }
}