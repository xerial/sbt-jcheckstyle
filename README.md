# sbt-jcheckstyle
A sbt plugin for checking Java code styles with [checkstyle](https://checkstyle.org/).

[![CI](https://github.com/xerial/sbt-jcheckstyle/actions/workflows/test.yml/badge.svg)](https://github.com/xerial/sbt-jcheckstyle/actions/workflows/test.yml)
[![Maven Central](https://img.shields.io/maven-central/v/org.xerial.sbt/sbt-jcheckstyle_sbt2_3?label=Maven%20Central)](https://central.sonatype.com/artifact/org.xerial.sbt/sbt-jcheckstyle_sbt2_3)

## Compatibility

| sbt-jcheckstyle | sbt      | Scala (build) |
|-----------------|----------|---------------|
| 0.3.x           | 2.x      | 3             |
| 0.2.x           | 1.x      | 2.12          |

## Usage

Add sbt-jcheckstyle plugin to your `project/plugins.sbt`, then run the `jcheckStyle` task:

**project/plugins.sbt**
```scala
// sbt 2.x (use sbt-jcheckstyle 0.2.x for sbt 1.x)
addSbtPlugin("org.xerial.sbt" % "sbt-jcheckstyle" % "0.3.0")
```

```
# Check code style of java codes
$ sbt jcheckStyle

# Check code style of java test codes
$ sbt Test/jcheckStyle
```

### Run style check before compilation

Add the following sbt settings:
```scala
Compile / compile := (Compile / compile).dependsOn(Compile / jcheckStyle).value
Test / compile    := (Test / compile).dependsOn(Test / jcheckStyle).value
```

## Style configuration

To configure Java code style, edit `jcheckStyleConfig` setting. In default, it uses Google's Java style:

```scala
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
```scala
jcheckStyleConfig := "checkstyle.xml"
```
