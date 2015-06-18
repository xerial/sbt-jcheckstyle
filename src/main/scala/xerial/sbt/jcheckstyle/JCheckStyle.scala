package xerial.sbt.jcheckstyle

import com.puppycrawl.tools.checkstyle.api.{AuditEvent, AuditListener, SeverityLevel}
import com.puppycrawl.tools.checkstyle.{Checker, ConfigurationLoader, PropertiesExpander}
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

import scala.collection.JavaConverters._

object JCheckStyle extends AutoPlugin {

  trait JCheckStyleKeys {
    val jcheckStyleConfig = settingKey[File]("Path checkstyle configuration file. The default is checkstyle.xml")
    val jcheckStyleEnforce = settingKey[Boolean]("Enforce style check. default = true")
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
    jcheckStyleConfig := baseDirectory.value / "checkstyle.xml",
    jcheckStyleEnforce := true,
    jcheckStyle in Compile <<= runCheckStyle(Compile),
    jcheckStyle in Test <<= runCheckStyle(Test)
  )

  private def relPath(file: File, base: File): File =
    file.relativeTo(base).getOrElse(file)

  def runCheckStyle(conf: Configuration): Def.Initialize[Task[Boolean]] = Def.task {
    val log = streams.value.log
    val javaSrcDir = (javaSource in conf).value
    log.info(s"Running checkstyle: ${relPath(javaSrcDir, baseDirectory.value)}")
    val configFile = jcheckStyleConfig.value
    if (!configFile.exists()) {
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
      if(totalNumberOfErrors > 0) {
        if(jcheckStyleEnforce.value) {
          sys.error(s"Found ${totalNumberOfErrors} style error(s)")
        }
      }
      else {
        log.info(s"checkstyle has succeeded")
      }
    }
    finally {
      checker.destroy()
    }
    true
  }

  class StyleCheckListener(baseDir: File, log: Logger) extends AuditListener {
    override def addError(evt: AuditEvent): Unit = {

      def message: String = s"${relPath(new File(evt.getFileName), baseDir)}:${evt.getLine}: ${evt.getMessage}"

      evt.getSeverityLevel match {
        case SeverityLevel.ERROR =>
          log.error(message)
        case SeverityLevel.WARNING =>
          log.warn(message)
        case _ =>
      }
    }

    override def fileStarted(evt: AuditEvent): Unit = {
      log.debug(s"checking ${relPath(new File(evt.getFileName), baseDir)}")
    }
    override def auditStarted(evt: AuditEvent): Unit = {}

    override def fileFinished(evt: AuditEvent): Unit = {}

    override def addException(evt: AuditEvent, throwable: Throwable): Unit = {
    }

    override def auditFinished(evt: AuditEvent): Unit = {

    }
  }


}
