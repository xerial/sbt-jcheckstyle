organization := "org.xerial.sbt"
name := "sbt-jcheckstyle"
organizationName := "Xerial project"
organizationHomepage := Some(new URL("http://xerial.org/"))

description := "A sbt plugin for checking Java code styles"

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked")

sbtPlugin := true
publishMavenStyle := true
scalacOptions += "-deprecation"

scriptedSettings
scriptedBufferLog := false
scriptedLaunchOpts ++= {
  import scala.collection.JavaConverters._
  val memOpt: Seq[String] = management
    .ManagementFactory
    .getRuntimeMXBean()
    .getInputArguments()
    .asScala
    .filter(a => Seq("-Xmx", "-Xms").contains(a) || a.startsWith("-XX"))
    .toSeq
  memOpt ++ Seq(s"-Dplugin.version=${version.value}")
}

libraryDependencies := Seq(
  "com.puppycrawl.tools" % "checkstyle" % "7.6"
)
