package parsers

import java.time.LocalTime

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
/**
  * Created by connor.jennings on 4/3/17.
  */
trait CanParseJava8TimeJSON {
  implicit val brevisLocalTimeReads: Reads[LocalTime] = new Reads[LocalTime] {
    override def reads(json: JsValue): JsResult[LocalTime] = {
      JsSuccess(LocalTime.parse(json.as[String]))
    }
  }

  implicit val brevisLocalTimeWrites: Writes[LocalTime] = new Writes[LocalTime] {
    override def writes(o: LocalTime): JsValue = {
      JsString(o.toString)
    }
  }
}
