import sbt.Keys._

lazy val root = (project in file("."))
  .enablePlugins(PlayService, PlayLayoutPlugin, JavaAppPackaging)
  .settings(
    name := "github-rank-challenge",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      guice,
      ws,
      ehcache
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )

