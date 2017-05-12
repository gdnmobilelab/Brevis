package services

import com.google.inject.Inject
import db.UserDAO
import models.BrevisUser

/**
  * Created by connor.jennings on 4/4/17.
  */
class BrevisUserService @Inject() (userDAO: UserDAO) {
  def getUsers(): Seq[BrevisUser] = {
    userDAO.getUsers()
  }
}
