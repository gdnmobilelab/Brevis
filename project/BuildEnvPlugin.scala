import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin


object BuildEnvPlugin extends AutoPlugin {

  // make sure it triggers automatically
  override def trigger = AllRequirements
  override def requires = JvmPlugin

  object autoImport {
    object BuildEnv extends Enumeration {
      val Production, Stage, StageDEBUG, Test, Developement = Value
    }

    val buildEnv = settingKey[BuildEnv.Value]("the current build environment")
  }
  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    buildEnv := {
      sys.props.get("env")
      .orElse(sys.env.get("BUILD_ENV"))
      .flatMap {
        case "prod" => Some(BuildEnv.Production)
        case "stage" => Some(BuildEnv.Stage)
        case "stage-debug" => Some(BuildEnv.StageDEBUG)
        case "test" => Some(BuildEnv.Test)
        case "dev" => Some(BuildEnv.Developement)
        case unkown => None
      }
      .getOrElse(BuildEnv.Developement)
    },
    // give feed back
    onLoadMessage := {
      // depend on the old message as well
      val defaultMessage = onLoadMessage.value
      val env = buildEnv.value
      s"""|$defaultMessage
          |Running in build environment: $env""".stripMargin
    }
  )
}