package xerial.sbt.jcheckstyle

import com.puppycrawl.tools.checkstyle.api.{AuditEvent, AuditListener, SeverityLevel}
import com.puppycrawl.tools.checkstyle.{PackageNamesLoader, Checker, ConfigurationLoader, PropertiesExpander}
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

import scala.collection.JavaConverters._

object JCheckStyle extends AutoPlugin {

  trait JCheckStyleKeys {
    val jcheckStyleConfig = settingKey[String]("Check style type: google (default), facebook, sun or path to checkstyle.xml")
    val jcheckStyleStrict = settingKey[Boolean]("Issue an error when style check fails. default = true")
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
    jcheckStyleConfig := "google",
    jcheckStyleStrict := true,
    jcheckStyle in Compile := runCheckStyle(Compile).value,
    jcheckStyle in Test := runCheckStyle(Test).value
  )

  private def relPath(file: File, base: File): File =
    file.relativeTo(base).getOrElse(file)


  private def findStyleFile(style:String, targetDir:File): File = {
    val styleResource = this.getClass.getResource(s"/xerial/sbt/jcheckstyle/${style}.xml")
    if(styleResource != null) {
      val in = styleResource.openStream()
      try {
        val configFileBytes = IO.readBytes(in)
        val path = targetDir / "jcheckstyle" / s"${style.toLowerCase}.xml"
        path.getParentFile.mkdirs()
        IO.write(path, configFileBytes)
        path
      }
      finally {
        in.close()
      }
    }
    else {
      new File(style)
    }
  }

  /** Compares the given specification version to the specification version of the platform.
    *
    * This code is copied from https://github.com/som-snytt/scala/blob/10336958aba9b8af5f9127a4dc21c0899836ff8d/src/library/scala/util/Properties.scala#L185
    *
    *  @param version a specification version number (legacy forms acceptable)
    *  @return `true` if the specification version of the current runtime
    *    is equal to or higher than the version denoted by the given string.
    *  @throws NumberFormatException if the given string is not a version string
    *
    *  @example {{{
    *  // In this example, the runtime's Java specification is assumed to be at version 8.
    *  isJavaAtLeast("1.8")            // true
    *  isJavaAtLeast("8")              // true
    *  isJavaAtLeast("9")              // false
    *  isJavaAtLeast("9.1")            // false
    *  isJavaAtLeast("1.9")            // throws
    *  }}}
    */
  private def isJavaAtLeast(version: String): Boolean = {
    def versionOf(s: String, depth: Int): (Int, String) =
      s.indexOf('.') match {
        case 0 =>
          (-2, s.substring(1))
        case 1 if depth == 0 && s.charAt(0) == '1' =>
          val r0 = s.substring(2)
          val (v, r) = versionOf(r0, 1)
          val n = if (v > 8 || r0.isEmpty) -2 else v   // accept 1.8, not 1.9 or 1.
          (n, r)
        case -1 =>
          val n = if (!s.isEmpty) s.toInt else if (depth == 0) -2 else 0
          (n, "")
        case i  =>
          val r = s.substring(i + 1)
          val n = if (depth < 2 && r.isEmpty) -2 else s.substring(0, i).toInt
          (n, r)
      }
    def compareVersions(s: String, v: String, depth: Int): Int = {
      if (depth >= 3) 0
      else {
        val (sn, srest) = versionOf(s, depth)
        val (vn, vrest) = versionOf(v, depth)
        if (vn < 0) -2
        else if (sn < vn) -1
        else if (sn > vn) 1
        else compareVersions(srest, vrest, depth + 1)
      }
    }
    val javaSpecVersion = System.getProperty("java.specification.version", "")
    compareVersions(javaSpecVersion, version, 0) match {
      case -2 => throw new NumberFormatException(s"Not a version: $version")
      case i  => i >= 0
    }
  }

  def runCheckStyle(conf: Configuration): Def.Initialize[Task[Boolean]] = Def.task {
    val log = streams.value.log
    val baseDir = baseDirectory.value
    val javaSrcDir = (javaSource in conf).value
    val isStrict = jcheckStyleStrict.value
    val config = jcheckStyleConfig.value
    val sourceFiles = (sources in conf).value
    val targetDir = target.value

    if (!isJavaAtLeast("1.7")) {
      log.warn(s"checkstyle requires Java 1.7 or higher.")
    }
    else {
      log.info(s"Running checkstyle: ${relPath(javaSrcDir, baseDir)}")

      // Find checkstyle configuration
      val styleFile = findStyleFile(config, targetDir)
      if (!styleFile.exists()) {
        sys.error(s"${styleFile} does not exist. jcheckStyleConfig must be airlift, google, sun or path to config.xml")
      }

      log.info(s"Using checkstyle configuration: ${config}")

      val javaFiles = sourceFiles.filter(_.getName endsWith ".java").asJava
      val loader = ConfigurationLoader.loadConfiguration(styleFile.getPath, new PropertiesExpander(System.getProperties))
      val checker = new Checker()
      try {
        checker.setModuleClassLoader(classOf[Checker].getClassLoader)
        checker.configure(loader)
        checker.addListener(new StyleCheckListener(baseDir, log))
        val totalNumberOfErrors = checker.process(javaFiles)
        if (totalNumberOfErrors > 0) {
          if (isStrict) {
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
