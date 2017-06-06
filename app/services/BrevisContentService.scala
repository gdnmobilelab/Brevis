package services

import com.google.inject.Inject
import db.ContentDAO
import models.BrevisContent

/**
  * Created by connor.jennings on 3/21/17.
  */
class BrevisContentService @Inject() (contentDAO: ContentDAO) {

  def insertBrevisContent(brevisContent: BrevisContent): BrevisContent = {
    contentDAO.insertContent(brevisContent)
  }

  def getContentById(id: String): BrevisContent = {
    contentDAO.getContentById(id)
  }

  def getContentForActiveBrief(): Seq[BrevisContent] = {
    contentDAO.getContentForActiveBrief()
  }
}
