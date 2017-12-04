organization := "org.xerial.sbt"
name := "sbt-jcheckstyle"
organizationName := "Xerial project"
organizationHomepage := Some(new URL("http://xerial.org/"))

description := "A sbt plugin for checking Java code styles"

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked")

sbtPlugin := true
scalaVersion := "2.12.3"
sbtVersion := "1.0.0-RC3"
publishMavenStyle := true
scalacOptions += "-deprecation"

enablePlugins(ScriptedPlugin)
scriptedBufferLog := false
scriptedLaunchOpts ++= {
  import scala.collection.JavaConverters._
  val memOpt: Seq[String] = management
    .ManagementFactory
    .getRuntimeMXBean()
    .getInputArguments()
    .asScala
    .filter(a => Seq("scala.ext.dirs", "-Xmx", "-Xms").exists(a.contains) || a.startsWith("-XX"))
    .toSeq
  memOpt ++ Seq(s"-Dplugin.version=${version.value}")
}

libraryDependencies ++= Seq(
  "com.puppycrawl.tools" % "checkstyle" % "8.5"
)

