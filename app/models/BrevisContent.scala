package models

import com.gu.contentapi.client.model.v1.{Content, Tag}
import play.api.Logger

/**
  * Created by connor.jennings on 3/21/17.
  */

case class BrevisSimilarContent(
  id: String,
  score: Double
)

case class BrevisAuthor (
  id: String,
  name: String,
  webUrl: String,
  email: Option[String]
)

case class BrevisContentTag(
  id: String,
  tagType: String,
  sectionId: String,
  sectionName: String,
  webTitle: String,
  webUrl: String
)

object BrevisContentTag {
  def fromTag(t: Tag): BrevisContentTag = {
    BrevisContentTag(
      id = t.id,
      tagType = t.`type`.name,
      sectionId = t.sectionId.get,
      sectionName = t.sectionName.get,
      webTitle = t.webTitle,
      webUrl =  t.webUrl
    )
  }
}

case class BrevisContent (
  id: Option[String],
  path: String,
  contentType: String,
  headline: String,
  sectionId: String,
  sectionName: String,
  webPublicationDateISO: String,
  webPublicationDateTimestamp: Long,
  tags: Seq[BrevisContentTag],
  webUrl: String,
  standfirst: String,
  authors: Seq[BrevisAuthor],
  creatorName: String,
  byline: String,
  bodyText: String,
  main: String,
  bodyHtml: String,
  wordCount: Int,
  productionOffice: String,
  templatedHTML: Option[String] = None,
  similarContent: Seq[BrevisSimilarContent] = Nil
)

object BrevisContent {
  // Only support tags with sectionId at the moment (arbitrary, just don't want to deal with it at the moment)
  // These include "TYPE", "CONTRIBUTOR", "TRACKING", "TONE
  def filterOutUnsupportedTags(t: Tag) = {
    t.sectionId.isDefined
  }

  def apply(content: Content): BrevisContent = {
    apply(None, content)
  }

  def apply(id: Option[String], content: Content): BrevisContent = {
    val authors = content.tags.filter(t => t.`type`.name == "Contributor")
                  .map(t => BrevisAuthor(t.id, t.webTitle, t.webUrl, t.emailAddress))

    val tagsToUse = content.tags.filter(filterOutUnsupportedTags).map(BrevisContentTag.fromTag)

    Logger.debug(s"Creating Brevis Content: ${content.id}")

    val createdBy = {
      if(content.blocks.get.main.isDefined) {
        s"${content.blocks.get.main.get.createdBy.get.firstName.get} ${content.blocks.get.main.get.createdBy.get.lastName.get}"
      } else {
        s"${content.blocks.get.body.get.head.createdBy.get.firstName.get} ${content.blocks.get.body.get.head.createdBy.get.lastName.get}"
      }
    }

    BrevisContent(
      id = id,
      path = content.id,
      contentType = content.`type`.name,
      headline = content.fields.get.headline.get,
      sectionId = content.sectionId.get,
      sectionName = content.sectionName.get,
      webPublicationDateISO = content.webPublicationDate.get.iso8601,
      webPublicationDateTimestamp = content.webPublicationDate.get.dateTime,
      tags = tagsToUse,
      webUrl = content.webUrl,
      standfirst = content.fields.get.standfirst.get,
      authors = authors,
      creatorName = createdBy,
      byline = content.fields.get.byline.getOrElse("The Guardian"),
      bodyText = content.fields.get.bodyText.get,
      main = content.fields.get.main.get,
      bodyHtml = content.fields.get.body.get,
      wordCount = content.fields.get.wordcount.get,
      productionOffice = content.fields.get.productionOffice.get.name
    )
  }
}