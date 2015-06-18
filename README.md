# sbt-jcheckstyle
A sbt plugin for checking Java code styles with [checkstyle](http://checkstyle.sourceforge.net/).

## Usage

Add sbt-jcheckstyle plugin to your `project/plugins.sbt`, then run `jcheckStyle` task:
**project/plugins.sbt**
```
addSbtPlugin("org.xerial.sbt" % "sbt-jcheckstyle" % "0.1.0")
```

```
# Check code style of java codes
$ sbt jcheckStyle

# Check code style of java test codes
$ sbt test:jcheckStyle
```

## Style configuration

To configure Java code style, edit `jcheckStyleConfig` setting. In default, it uses Google's Java style:

```
jcheckStyleConfig := "google" // or "facebook", "sun", or path to your checkstyle.xml
```

Here is the list of the available styles:

* `google`:  [Google's Java Style](https://google-styleguide.googlecode.com/svn-history/r130/trunk/javaguide.html). [checkstyle.xml]
(https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml)
* `facebook` : [Code style used in Facebook Presto](https://github.com/facebook/presto/blob/master/src/checkstyle/checks.xml)
* `sun`: [Code Conventions for the Java TM Programming Language](http://www.oracle.com/technetwork/java/codeconvtoc-136057.html).
[checkstyle.xml](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/sun_checks.xml)

Or you can specify your own configuration:
```
jcheckStyelConfig := "checkstyle.xml"
```
