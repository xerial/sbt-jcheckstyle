package xerial.sbt.jcheckstyle

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

/**
 *
 */
class CheckStyle extends AutoPlugin
{
  trait CheckStyleKeys {
    val checkStyleConfig = settingKey[File]("Path to checkstyle-config.xml file")
    val checkStyle = taskKey[Boolean]("Run checkstyle")

  }

  object CheckStyleKeys extends CheckStyleKeys {
  }

  object autoImport extends CheckStyleKeys {
  }

  override def trigger = allRequirements
  override def requires = JvmPlugin
  override def projectSettings = checkStyleSettings

  import autoImport._

  lazy val checkStyleSettings = Seq[Setting[_]](
    checkStyleConfig := baseDirectory.value / "checkstyle-config.xml",
    checkStyle := {

      true
    }
  )
}
