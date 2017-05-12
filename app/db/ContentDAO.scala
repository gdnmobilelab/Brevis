package db

import java.util.UUID

import anorm._
import com.google.inject.Inject
import db.dbparsers.CanParseBrevisContentResultSet
import models.BrevisContent
import parsers.{CanParseBrevisAuthorJSON, CanParseBrevisContentTagJSON}
import play.api.db.Database
import play.api.libs.json.Json

/**
  * Created by connor.jennings on 3/20/17.
  */

// Julia Cheng just shaking her head in shame
class ContentDAO @Inject()(db: Database) extends CanParseBrevisAuthorJSON
with CanParseBrevisContentTagJSON with CanParseBrevisContentResultSet {


  def insertContent(brevisContent: BrevisContent): BrevisContent = {
    db.withConnection { implicit conn =>
      SQL(
        """
        select * from p_InsertBrevisContent(
          {id},
          {path},
          {contentType},
          {headline},
          {sectionId},
          {sectionName},
          {webPublicationDateISO},
          {webPublicationDateTimestamp},
          {tags},
          {webUrl},
          {standfirst},
          {authors},
          {creatorName},
          {byline},
          {main},
          {bodyText},
          {bodyHtml},
          {wordCount},
          {productionOffice})
        """).on(
        'id -> brevisContent.id.getOrElse(UUID.randomUUID().toString),
        'path -> brevisContent.path,
        'contentType -> brevisContent.contentType,
        'headline -> brevisContent.headline,
        'sectionId -> brevisContent.sectionId,
        'sectionName -> brevisContent.sectionName,
        'webPublicationDateISO -> brevisContent.webPublicationDateISO,
        'webPublicationDateTimestamp -> brevisContent.webPublicationDateTimestamp,
        'tags -> Json.toJson(brevisContent.tags).toString,
        'webUrl -> brevisContent.webUrl,
        'standfirst -> brevisContent.standfirst,
        'authors -> Json.toJson(brevisContent.authors).toString,
        'creatorName -> brevisContent.creatorName,
        'byline -> brevisContent.byline,
        'main -> brevisContent.main,
        'bodyText -> brevisContent.bodyText,
        'bodyHtml -> brevisContent.bodyHtml,
        'wordCount -> brevisContent.wordCount,
        'productionOffice -> brevisContent.productionOffice
      ).as(brevisContentResultSetParser.single)

    }
  }

  def getContentById(id: String): BrevisContent = {
    db.withConnection { implicit conn =>
      SQL("select * from p_GetBrevisContent({id})").on('id -> id).as(brevisContentResultSetParser.single)
    }
  }

  def getContentForActiveBrief(): Seq[BrevisContent] = {
    db.withConnection { implicit conn =>
      SQL("select * from p_GetBrevisContentFromActiveBrief()").as(brevisContentResultSetParser.*)
    }
  }
}