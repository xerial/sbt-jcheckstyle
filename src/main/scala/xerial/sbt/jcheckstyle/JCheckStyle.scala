package xerial.sbt.jcheckstyle

import com.puppycrawl.tools.checkstyle.api.{AuditEvent, AuditListener}
import com.puppycrawl.tools.checkstyle.{PropertiesExpander, ConfigurationLoader, Checker}
import sbt.Project.Initialize
import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import scala.collection.JavaConverters._

/**
 *
 */
object JCheckStyle extends AutoPlugin
{
  trait JCheckStyleKeys {
    val jcheckStyleConfig = settingKey[File]("Path to checkstyle-config.xml file")
    val jcheckStyle = taskKey[Boolean]("Run checkstyle")
  }

  object JCheckStyleKeys extends JCheckStyleKeys {
  }

  object autoImport extends JCheckStyleKeys {
  }

  override def trigger = allRequirements
  override def requires = JvmPlugin
  override def projectSettings = jcheckStyleSettings

  import autoImport._

  lazy val jcheckStyleSettings = Seq[Setting[_]](
    jcheckStyleConfig := baseDirectory.value / "checkstyle-config.xml",
    jcheckStyle in Compile <<= runCheckStyle(Compile),
    jcheckStyle in Test <<= runCheckStyle(Test)
  )

  private def relPath(file:File, base:File): File =
    file.relativeTo(base).getOrElse(file)

  def runCheckStyle(conf:Configuration) : Def.Initialize[Task[Boolean]] = Def.task {
    val log = streams.value.log
    val javaSrcDir = (javaSource in conf).value
    log.info(s"Running checkstyle: ${relPath(javaSrcDir, baseDirectory.value)}")
    val configFile = jcheckStyleConfig.value
    if(!configFile.exists()) {
      sys.error(s"${configFile} does not exist. Specify the config file path with jcheckStyleConfig setting")
    }

    val javaFiles = (sources in conf).value.filter(_.getName endsWith ".java").asJava
    val loader = ConfigurationLoader.loadConfiguration(jcheckStyleConfig.value.getPath, new PropertiesExpander(System.getProperties))
    val checker = new Checker()
    try {
      checker.setModuleClassLoader(classOf[Checker].getClassLoader)
      checker.configure(loader)
      checker.addListener(new StyleCheckListener(baseDirectory.value, log))
      val totalNumberOfErrors = checker.process(javaFiles)
    }
    finally {
      checker.destroy()
    }

    true
  }


  class StyleCheckListener(baseDir:File, log:Logger) extends AuditListener {
    override def addError(evt: AuditEvent): Unit = {}
    override def fileStarted(evt: AuditEvent): Unit = {
      log.info(s"checking ${relPath(new File(evt.getFileName), baseDir)}")
    }
    override def auditStarted(evt: AuditEvent): Unit = {}
    override def fileFinished(evt: AuditEvent): Unit = {}
    override def addException(evt: AuditEvent, throwable: Throwable): Unit = {
    }
    override def auditFinished(evt: AuditEvent): Unit = {}
  }



}
