sonatypeProfileName := "org.xerial"

pomExtra := {
  <url>http://xerial.org/</url>
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:github.com/xerial/sbt-jcheckstyle.git</connection>
      <developerConnection>scm:git:git@github.com:xerial/sbt-jcheckstyle.git</developerConnection>
      <url>github.com/xerial/sbt-jcheckstyle.git</url>
    </scm>
    <developers>
      <developer>
        <id>leo</id>
        <name>Taro L. Saito</name>
        <url>http://xerial.org/leo</url>
      </developer>
    </developers>
}

import ReleaseTransformations._

releaseTagName := {(version in ThisBuild).value}

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