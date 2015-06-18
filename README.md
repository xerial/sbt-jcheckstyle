# sbt-jcheckstyle
A sbt plugin for checking Java code styles

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
