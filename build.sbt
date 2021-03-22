import sbt.Keys.libraryDependencies

scalacOptions += "-Ymacro-annotations"

lazy val root = (project in file("."))
  .settings(
    name := "scala-dev-mooc",
    version := "0.1",
    scalaVersion := "2.13.3",
    libraryDependencies ++= Dependencies.http4sServer,
    libraryDependencies ++= Dependencies.pureconfig,
    libraryDependencies ++= Dependencies.circe,
    libraryDependencies ++= Dependencies.zio,
    libraryDependencies ++= Dependencies.tapir,
    libraryDependencies ++= Dependencies.grpc,
    libraryDependencies ++= Seq(
      Dependencies.akka,
      Dependencies.logback,
      Dependencies.jacksonDatabind
    ),
    addCompilerPlugin(Dependencies.kindProjector)
  )

PB.targets in Compile := Seq(
  scalapb.gen(grpc = true) -> (sourceManaged in Compile).value / "scalapb",
  scalapb.zio_grpc.ZioCodeGenerator -> (sourceManaged in Compile).value / "scalapb"
)