Global / onChangedBuildSource := ReloadOnSourceChanges

enablePlugins(SbtPlugin)

val SCALA_3 = "3.8.4"
ThisBuild / scalaVersion := SCALA_3

// Build the plugin for sbt 2.x (Scala 3). The meta-build itself runs on sbt 1.11.x.
pluginCrossBuild / sbtVersion := "2.0.0"

// Derive the version from git tags (e.g. tag "0.3.0" -> version 0.3.0)
ThisBuild / dynverVTagPrefix        := false
ThisBuild / dynverSeparator         := "-"
ThisBuild / dynverSonatypeSnapshots := true

organization         := "org.xerial.sbt"
organizationName     := "Xerial project"
name                 := "sbt-jcheckstyle"
organizationHomepage := Some(url("http://xerial.org/"))
description          := "A sbt plugin for checking Java code styles with checkstyle"

publishMavenStyle      := true
Test / publishArtifact := false
pomIncludeRepository   := { _ => false }

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature")

scriptedBufferLog := false
scriptedLaunchOpts ++= {
  import scala.collection.JavaConverters._
  management.ManagementFactory
    .getRuntimeMXBean()
    .getInputArguments()
    .asScala
    .filter(a => Seq("-Xmx", "-Xms").exists(a.startsWith) || a.startsWith("-XX"))
    .toSeq ++ Seq("-Dplugin.version=" + version.value)
}

libraryDependencies ++= Seq(
  "com.puppycrawl.tools" % "checkstyle" % "8.5"
)

// Publishing metadata
homepage := Some(url("https://github.com/xerial/sbt-jcheckstyle"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/xerial/sbt-jcheckstyle"),
    "scm:git@github.com:xerial/sbt-jcheckstyle.git"
  )
)
developers := List(
  Developer(id = "leo", name = "Taro L. Saito", email = "leo@xerial.org", url = url("http://xerial.org/leo"))
)
licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

// Publish to Sonatype Central (built-in to sbt 1.11+/2.x via sonaUpload / sonaRelease)
publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}
