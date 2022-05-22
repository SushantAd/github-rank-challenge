import sbt.Keys._

lazy val root = (project in file("."))
  .enablePlugins(PlayService, PlayLayoutPlugin, JavaAppPackaging)
  .settings(
    name := "github-rank-challenge",
    scalaVersion := "2.12.8",
    PlayKeys.playDefaultPort := 8080,
    libraryDependencies ++= Seq(
      guice,
      ws,
      ehcache,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
      "com.github.ben-manes.caffeine" % "jcache" % "2.5.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )

