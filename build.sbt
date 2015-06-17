organization := "org.xerial.sbt"
name := "sbt-jcheckstyle"
organizationName := "Xerial project"
organizationHomepage := Some(new URL("http://xerial.org/"))

description := "A sbt plugin for checking Java code styles"

sbtPlugin := true
publishMavenStyle := false
scalacOptions += "-deprecation"

scriptedSettings
scriptedBufferLog := false
scriptedLaunchOpts ++= {
      import scala.collection.JavaConverters._
      val memOpt : Seq[String] = management.ManagementFactory.getRuntimeMXBean().getInputArguments().asScala.filter(a => Seq("-Xmx","-Xms").contains(a) || a.startsWith("-XX")).toSeq
      memOpt ++ Seq(s"-Dplugin.version=${version.value}")
    }


import ReleaseTransformations._

releaseTagName := { (version in ThisBuild).value }
releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      ReleaseStep(action = Command.process("publishSigned", _)),
      setNextVersion,
      commitNextVersion,
      ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
      pushChanges
)
