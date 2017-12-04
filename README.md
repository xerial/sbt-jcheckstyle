# sbt-jcheckstyle
A sbt plugin for checking Java code styles with [checkstyle](http://checkstyle.sourceforge.net/).

## Usage

Add sbt-jcheckstyle plugin to your `project/plugins.sbt`, then run `jcheckStyle` task:
**project/plugins.sbt**
```
// For sbt-0.13.x and 1.0.x
addSbtPlugin("org.xerial.sbt" % "sbt-jcheckstyle" % "0.2.0")
```

```
# Check code style of java codes
$ sbt jcheckStyle

# Check code style of java test codes
$ sbt test:jcheckStyle
```

### Run style check before compilation

Add the following sbt settings:
```
compile in Compile <<= (compile in Compile) dependsOn (jcheckStyle in Compile)
compile in Test <<= (compile in Test) dependsOn (jcheckStyle in Test)
```

## Style configuration

To configure Java code style, edit `jcheckStyleConfig` setting. In default, it uses Google's Java style:

```
jcheckStyleConfig := "google" // or "facebook", "sun" or path to your checkstyle.xml
```

Here is the list of the available styles:

* `google`:  [Google's Java Style](https://google.github.io/styleguide/javaguide.html). [checkstyle.xml](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml)
  * [IntelliJ setting file](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)
* `facebook` : [Code style used in Facebook Presto](https://github.com/facebook/presto/blob/master/src/checkstyle/checks.xml)
  * [IntelliJ setting file](https://raw.githubusercontent.com/airlift/codestyle/master/IntelliJIdea13/Airlift.xml)
* `sun`: [Code Conventions for the Java TM Programming Language](http://www.oracle.com/technetwork/java/codeconvtoc-136057.html).
[checkstyle.xml](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/sun_checks.xml)

Or you can specify your own configuration:
```
jcheckStyleConfig := "checkstyle.xml"
```
