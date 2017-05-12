package services

import com.google.inject.Inject
import db.{BriefDAO, ContentDAO}
import models.{BrevisBrief, BrevisContent}

import scala.concurrent.Future

/**
  * Created by connor.jennings on 3/27/17.
  */
class BrevisBriefService @Inject() (
  briefDAO: BriefDAO) {

  def getActiveBrief(): Option[BrevisBrief] = {
    briefDAO.getActiveBrief()
  }

  def insertBrevisBrief(brevisBrief: BrevisBrief): BrevisBrief = {
    briefDAO.insertBrief(brevisBrief)
  }

  def makeBriefActive(briefId: String): BrevisBrief = {
    briefDAO.makeBriefActive(briefId)
  }

  def addContentToBrief(contentId: String, briefId: String): Boolean = {
    briefDAO.addContentToBrief(contentId = contentId, briefId = briefId)
  }
}
