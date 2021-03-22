import Dependencies.versions.{AkkaVersion, CirceVersion, GrpcVersion, Http4sVersion, KindProjectorVersion, PureconfigVersion, TapirVersion, ZioVersion}
import sbt.ModuleID
import sbt._

object Dependencies {
  object versions {
    lazy val KindProjectorVersion = "0.10.3"
    lazy val Http4sVersion = "0.21.7"
    lazy val CirceVersion = "0.13.0"
    lazy val TapirVersion = "0.17.8"
    lazy val ZioVersion = "1.0.4"
    lazy val PureconfigVersion = "0.12.3"
    val AkkaVersion = "2.6.10"
    lazy val GrpcVersion = "1.36.0"
  }

  lazy val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-generic" % CirceVersion,
    "io.circe" %% "circe-generic-extras"% CirceVersion,
    "io.circe" %% "circe-parser" % CirceVersion
  )

  lazy val http4sServer: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-dsl"          % Http4sVersion,
    "org.http4s" %% "http4s-circe"        % Http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % Http4sVersion
  )

  lazy val zio: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio" % ZioVersion,
    "dev.zio" %% "zio-interop-cats" % "2.2.0.1",
    "dev.zio" %% "zio-streams" % "1.0.2",
    "dev.zio" %% "zio-kafka"   % "0.13.0",
    "dev.zio" %% "zio-logging-slf4j" % "0.5.6"
  )

  lazy val tapir: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-zio" % "0.17.8",
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-cats"               % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % TapirVersion
  )

  lazy val pureconfig: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig"             % PureconfigVersion,
    "com.github.pureconfig" %% "pureconfig-cats-effect" % PureconfigVersion
  )

  lazy val grpc: Seq[ModuleID] =  Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
    "io.grpc" % "grpc-netty" % GrpcVersion
  )


  lazy val akka = "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion

  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"

  lazy val kindProjector = "org.typelevel" %% "kind-projector" % KindProjectorVersion

  lazy val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.2"
}
