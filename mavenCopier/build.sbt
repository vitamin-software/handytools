name := "mavenCopier"

version := "1.0"

organization := "io.vitamin"

scalaVersion := "2.12.2"

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions += "-target:jvm-1.7"

libraryDependencies ++= Seq(
   "com.github.scopt" %% "scopt" % "3.5.0",
   "org.slf4j" % "slf4j-api" % "1.7.5",
   "org.slf4j" % "slf4j-simple" % "1.7.5"
)

mainClass in Compile := Some("io.vitamin.MavenCopier")
    