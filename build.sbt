scalaVersion := "2.13.3"

name := "scala-dev-mooc"

version := "0.1"


val AkkaVersion = "2.6.10"

libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
