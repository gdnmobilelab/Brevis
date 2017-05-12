package filters

import com.google.inject.Inject
import org.pac4j.play.filters.SecurityFilter
import play.api.http.HttpFilters
import play.filters.gzip.GzipFilter

/**
  * Created by connor.jennings on 3/30/17.
  */
class Filters @Inject()(
  securityFilter: SecurityFilter,
  loggingFilter: LoggingFilter,
  gzip: GzipFilter,
  loginRedirectFilter: LoginRedirectFilter) extends HttpFilters {

  //loginRedirectFilter,
  def filters = Seq(loggingFilter, loginRedirectFilter, securityFilter, gzip)
}
