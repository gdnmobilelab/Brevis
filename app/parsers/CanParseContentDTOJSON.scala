package parsers

import models.dto.ContentDTO
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by connor.jennings on 3/31/17.
  */
trait CanParseContentDTOJSON {
  implicit val contentDTOReads:Reads[ContentDTO] = (JsPath \ "id").read[String].map(ContentDTO.apply)
}
