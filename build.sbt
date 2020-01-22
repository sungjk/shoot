val dottyVersion = "0.21.0-RC1"

name := "shoot"
organization in ThisBuild := "io.sungjk"

val scala213Version = "2.13.0"

crossScalaVersions := Seq(scala213Version)

val circeVersion = "0.12.3"
val sttpVersion = "1.7.2"
val scalaCheckVersion = "1.14.3"
val scalaTestVersion = "3.1.0"

lazy val root = project.in(file("."))
  .settings(
    name := "shoot",
    version := "0.0.1",
    libraryDependencies ++= Seq(
        "io.circe" %% "circe-core" % circeVersion,
        "io.circe" %% "circe-generic" % circeVersion,
        "io.circe" %% "circe-parser" % circeVersion,
        "com.softwaremill.sttp" %% "core" % sttpVersion,
        "com.softwaremill.sttp" %% "circe" % sttpVersion,
        "com.softwaremill.sttp.client" %% "core" % "2.0.0-RC6",
        "com.novocode" % "junit-interface" % "0.11" % "test",
        // test
        "org.scalacheck" %% "scalacheck" % scalaCheckVersion % Test,
        "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    )
  )
