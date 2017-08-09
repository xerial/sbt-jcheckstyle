addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0-M1")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.6")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
