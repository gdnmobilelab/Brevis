package db.dbparsers

import anorm.SqlParser.{int, str}
import anorm.~
import models.{BrevisAuthor, BrevisContent, BrevisContentTag}
import parsers.{CanParseBrevisAuthorJSON, CanParseBrevisContentTagJSON}
import play.api.libs.json.Json

/**
  * Created by connor.jennings on 4/4/17.
  */
trait CanParseBrevisContentResultSet extends CanParseBrevisAuthorJSON with CanParseBrevisContentTagJSON {
  val brevisContentResultSetParser =
    str("brevis_content_id") ~
      str("brevis_content_path") ~
      str("brevis_content_contentType") ~
      str("brevis_content_headline") ~
      str("brevis_content_sectionId") ~
      str("brevis_content_sectionName") ~
      str("brevis_content_webPublicationDateISO") ~
      int("brevis_content_webPublicationDateTimestamp") ~
      str("brevis_content_tags") ~
      str("brevis_content_webUrl") ~
      str("brevis_content_standfirst") ~
      str("brevis_content_authors") ~
      str("brevis_content_creatorName") ~
      str("brevis_content_byline") ~
      str("brevis_content_main") ~
      str("brevis_content_bodyText") ~
      str("brevis_content_bodyHtml") ~
      int("brevis_content_wordCount") ~
      str("brevis_content_productionOffice") map {
      case
        id ~
          path ~
          contentType ~
          headline ~
          sectionId ~
          sectionName ~
          webPublicationDateISO ~
          webPublicationDateTimestamp ~
          tags ~
          webUrl ~
          standfirst ~
          authors ~
          creatorName ~
          byline ~
          main ~
          bodyText ~
          bodyHtml ~
          wordCount ~
          productionOffice
      => BrevisContent(
        id = Some(id),
        path = path,
        contentType = contentType,
        headline = headline,
        sectionId = sectionId,
        sectionName = sectionName,
        webPublicationDateISO = webPublicationDateISO,
        webPublicationDateTimestamp = webPublicationDateTimestamp,
        tags = Json.parse(tags).as[Seq[BrevisContentTag]],
        webUrl = webUrl,
        standfirst = standfirst,
        authors = Json.parse(authors).as[Seq[BrevisAuthor]],
        creatorName = creatorName,
        byline = byline,
        bodyText = bodyText,
        main = main,
        bodyHtml = bodyHtml,
        wordCount = wordCount,
        productionOffice = productionOffice
      )
    }

}
