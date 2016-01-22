name := """track-me"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies += filters

libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  cache,
  ws,
  specs2 % Test,
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "jp.t2v" %% "play2-auth" % "0.14.1",
  "com.github.t3hnar" % "scala-bcrypt_2.10" % "2.5"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

//fork in run := true