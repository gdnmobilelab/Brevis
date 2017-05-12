package filters

import javax.inject.Inject
import akka.stream.Materializer
import play.api.mvc.{Result, RequestHeader, Filter}
import play.api.Logger
import play.api.routing.Router.Tags
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class LoggingFilter @Inject() (implicit val mat: Materializer) extends Filter {
  def apply(nextFilter: RequestHeader => Future[Result])
    (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>

      (requestHeader.tags.get(Tags.RouteController), requestHeader.tags.get(Tags.RouteActionMethod)) match {
        case (Some(controller), Some(method)) =>
          val action = controller +
            "." + method
          val endTime = System.currentTimeMillis
          val requestTime = endTime - startTime

          Logger.info(s"${action} took ${requestTime}ms and returned ${result.header.status}")

          result.withHeaders("Request-Time" -> requestTime.toString)
        case _ =>
          result
      }
    }
  }
}