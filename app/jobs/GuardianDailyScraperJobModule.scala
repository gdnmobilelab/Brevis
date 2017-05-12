package jobs

import javax.inject.Singleton

import akka.actor.{ActorSystem, Props}
import akka.camel.Consumer
import com.google.inject.{AbstractModule, Inject}
import play.api.inject.ApplicationLifecycle
import services._
import util.CommuteRecommendationUtil


/**
  * Created by connor.jennings on 3/20/17.
  */
class GuardianDailyScraperModule extends AbstractModule {
  val shouldScrapeGuardianDaily = true

  override def configure() = {
    if (shouldScrapeGuardianDaily) {
      bind(classOf[GuardianDailyScraperJobActorSystem]).asEagerSingleton()
    }
  }
}

@Singleton
class GuardianDailyScraperJobActorSystem @Inject() (actorSystem: ActorSystem,
  lifecycle: ApplicationLifecycle,
  guardianDailyEmailBriefCreator: GuardianDailyEmailBriefCreator,
  pushService: FCMPushService
) {

  // Just scheduling your task using the injected ActorSystem
  actorSystem.actorOf(GuardianDailyScraperJobActor.props(
    guardianDailyEmailBriefCreator = guardianDailyEmailBriefCreator,
    pushService = pushService))

  // This is necessary to avoid thread leaks, specially if you are
  // using a custom ExecutionContext
  lifecycle.addStopHook{ () =>
    actorSystem.terminate()
  }
}

object GuardianDailyScraperJobActor {
  def props(
    guardianDailyEmailBriefCreator: GuardianDailyEmailBriefCreator,
    pushService: FCMPushService
  ) = {
    Props(new GuardianDailyScraperJobActor(
      guardianDailyEmailBriefCreator = guardianDailyEmailBriefCreator,
      pushService = pushService
    ))
  }
}

class GuardianDailyScraperJobActor(
  guardianDailyEmailBriefCreator: GuardianDailyEmailBriefCreator,
  pushService: FCMPushService
) extends Consumer {

  // UTF, execute at 8:30am
   def endpointUri = "quartz://example?cron=0+30+12,21+*+*+?"

  // Using this for testing
//    def endpointUri = "quartz://example?cron=30+*+*+*+*+?"


  def receive = {
    case msg =>
      guardianDailyEmailBriefCreator.create()

      pushService.notifyNewBrief()
  }
}