name := """soulmates-backend"""
organization := "eu.espeo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.2"

envVars in Test := Map("MONGO_HOST" -> "localhost")
envVars in Test := Map("MONGO_PORT" -> "12345")

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "org.mongodb.morphia" % "morphia" % "1.3.2"
libraryDependencies += "com.auth0" % "java-jwt" % "3.3.0"
libraryDependencies += "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "2.0.3" % Test

javaOptions in Test ++= Seq("-Dlogger.resource=logback-test.xml")
