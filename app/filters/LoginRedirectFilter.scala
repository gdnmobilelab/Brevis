package filters

import javax.inject.Inject

import akka.stream.Materializer
import org.pac4j.core.config.Config
import org.pac4j.core.context.Pac4jConstants
import org.pac4j.play.PlayWebContext
import org.pac4j.play.filters.SecurityFilter
import org.pac4j.play.java.SecureAction
import org.pac4j.play.store.PlaySessionStore
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{RequestHeader, Result}
import play.libs.concurrent.HttpExecutionContext

import scala.collection.mutable
import scala.compat.java8.FutureConverters._
import scala.concurrent.Future

/**
  * Created by connor.jennings on 4/19/17.
  */
class LoginRedirectFilter @Inject() (mat: Materializer, configuration: Configuration, playSessionStore: PlaySessionStore, config: Config, ec: HttpExecutionContext)
  extends SecurityFilter(mat, configuration, playSessionStore, config, ec) {

  override def apply(nextFilter: RequestHeader => Future[Result])
    (request: RequestHeader): Future[Result] = {

    findRule(request) match {
      case Some(rule) =>
        log.debug(s"Authentication needed for ${request.uri}")
        val webContext = new PlayWebContext(request, playSessionStore)
        val javaContext = webContext.getJavaContext
        val profiles = webContext.getSessionAttribute(Pac4jConstants.USER_PROFILES).asInstanceOf[java.util.LinkedHashMap[java.lang.String, Object]]

        if (rule.authorizers != "_anonymous_"
          && profiles == null
          && request.headers.toSimpleMap.get("X-Brevis-Session").isEmpty
          && request.getQueryString("login").isEmpty
          && !request.path.contains("/brevis/app/callback")) {
          Future.successful(Redirect("/brevis/app/login/"))
        } else {
          nextFilter(request)
        }

      case None =>
        log.debug(s"No authentication needed for ${request.uri}")
        nextFilter(request)
    }
  }
}