package xerial.sbt.jcheckstyle

import com.puppycrawl.tools.checkstyle.{PropertiesExpander, ConfigurationLoader, Checker}
import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import scala.collection.JavaConversions._

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

      val loader = ConfigurationLoader.loadConfiguration(checkStyleConfig.value.getPath, new PropertiesExpander(System.getProperties))
      val checker = new Checker()
      checker.configure(loader)
      checker.process(Seq.empty)

      true
    }
  )
}
