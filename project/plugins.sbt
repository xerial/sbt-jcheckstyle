
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.1")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.3")

libraryDependencies <+= sbtVersion("org.scala-sbt" % "scripted-plugin" % _)
