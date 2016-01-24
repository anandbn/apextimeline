name := """hello-play24"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "com.fasterxml.jackson.core" % "jackson-core" % "2.7.0",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.0",  
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.7.0"  
)


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


fork in run := true