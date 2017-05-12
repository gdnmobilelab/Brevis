package auth

import java.util

import com.google.inject.AbstractModule
import org.pac4j.oauth.config.OAuth20Configuration
//import GoogleLoginCallbackController
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.core.context.WebContext
import org.pac4j.core.profile.{CommonProfile, ProfileManager}
import org.pac4j.oauth.client.Google2Client
import org.pac4j.play.{CallbackController, LogoutController}
import org.pac4j.play.http.DefaultHttpActionAdapter
import org.pac4j.play.CallbackController
import org.pac4j.play.store.{PlayCacheSessionStore, PlaySessionStore}
import play.api.{Configuration, Environment}

/**
  * Created by connor.jennings on 3/30/17.
  */
class SecurityModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  val clientId = configuration.getString("brevis.googleClientId").get
  val clientSecret = configuration.getString("brevis.googleClientSecret").get
  val hostname = configuration.getString("brevis.host").get

  override def configure(): Unit = {
    val googleClient = new Google2Client(clientId, clientSecret)
    var oauthConf: OAuth20Configuration = googleClient.getConfiguration
    val params: java.util.Map[String, String] = oauthConf.getCustomParams

//    params.put("hd", "guardian.co.uk")
    oauthConf.setCustomParams(params)

    googleClient.setConfiguration(oauthConf)

    val clients = new Clients(hostname + "/brevis/app/callback", googleClient)
    val config = new Config(clients)

    config.setHttpActionAdapter(new DefaultHttpActionAdapter())

    bind(classOf[Config]).toInstance(config)

    bind(classOf[PlaySessionStore]).to(classOf[PostgresSessionStore])

    bind(classOf[CallbackController]).to(classOf[GoogleLoginCallbackController])

    val logoutController = new LogoutController()
    logoutController.setDefaultUrl(hostname + "/brevis/app/login/")
    bind(classOf[LogoutController]).toInstance(logoutController)
  }
}
