name := "BrevisCR"

version := "1.0"

lazy val `breviscr` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws, filters )

libraryDependencies ++= Seq(
  "com.gu" %% "content-api-client" % "11.10",
  "com.typesafe.akka" % "akka-camel_2.11" % "2.4.17",
  "org.apache.camel" % "camel-quartz" % "2.18.3",
  "org.jsoup" % "jsoup" % "1.10.2",
//  "org.mongodb.scala" %% "mongo-scala-driver" % "1.2.1",
  "com.typesafe.play" %% "anorm" % "2.5.3",
  "org.postgresql" % "postgresql" % "42.0.0",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.8.7",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.7",
//  "org.apache.logging.log4j" % "log4j-core" % "2.8.1",
//  "org.apache.logging.log4j" % "log4j-api" % "2.8.1",
//  "org.apache.logging.log4j" % "log4j-api-scala_2.11" % "2.8.1",
  "org.pac4j" % "pac4j" % "2.0.0-RC2",
  "org.pac4j" % "play-pac4j" % "3.0.0-RC2",
  "org.pac4j" % "pac4j-oauth" % "2.0.0-RC2",
  "com.mohiva" %% "play-html-compressor" % "0.6.3",
  "org.scala-lang.modules" % "scala-java8-compat_2.11" % "0.8.0",
  "com.google.api-client" % "google-api-client" % "1.22.0",
  "com.google.http-client" % "google-http-client" % "1.22.0"
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

unmanagedResourceDirectories in Test <+= baseDirectory ( _ /"target/web/public/test" )

resolvers ++= Seq(
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)

resolvers += Resolver.jcenterRepo

// Play specific
routesGenerator := InjectedRoutesGenerator

watchSources := (watchSources.value
  --- baseDirectory.value / "public"     ** "*").get

// Packaging
topLevelDirectory := None

packageName in Universal := {
  val env = buildEnv.value match {
    case BuildEnv.Stage => "STAGE"
    case BuildEnv.StageDEBUG => "STAGE-DEBUG"
    case BuildEnv.Production => "PROD"
    case _ => "DEV"
  }

  val name = (packageName in Universal).value
  def timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmm") format new java.util.Date()
  s"$env-$name-$timestamp"
}

mappings in Universal += {
  buildEnv.value match {
    case BuildEnv.Stage | BuildEnv.StageDEBUG =>
      file(s"Procfile-STAGE") -> "Procfile"
    case BuildEnv.Production =>
      file(s"Procfile-PROD") -> "Procfile"
  }
}

lazy val buildBrevisFrontend = taskKey[Unit]("Build the env")
buildBrevisFrontend := {
  val env = buildEnv.value match {
    case BuildEnv.Stage => "stage"
    case BuildEnv.StageDEBUG => "stageDebug"
    case BuildEnv.Production => "prod"
    case _ => "dev"
  }

  s"./public/brevis/node_modules/grunt/bin/grunt --gruntfile public/brevis/Gruntfile.js build:$env" !
}

lazy val buildBrevisDevFrontend = taskKey[Unit]("Builds the dev frontend")
buildBrevisDevFrontend := {
 "./public/brevis/node_modules/grunt/bin/grunt --gruntfile public/brevis/Gruntfile.js build:dev" !
}

lazy val buildBrevis = taskKey[Unit]("Builds stage")
buildBrevis := {
  Def.sequential(buildBrevisFrontend, (packageBin in Universal), buildBrevisDevFrontend).value
}