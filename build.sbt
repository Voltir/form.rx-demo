import com.lihaoyi.workbench.Plugin._

enablePlugins(ScalaJSPlugin)

workbenchSettings
//URL: http://localhost:12345/target/scala-2.11/classes/index-dev.html

lazy val root = project.in(file(".")).enablePlugins(ScalaJSPlugin)

name := "Formidable Demo"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.8.2",
  "com.lihaoyi" %%% "scalarx" % "0.3.0-SNAPSHOT",
  "com.lihaoyi" %%% "scalatags" % "0.5.2",
  "com.stabletechs" %%% "likelib" % "0.0.1-SNAPSHOT",
  "com.stabletechs" %%% "formidable" % "0.1.0-SNAPSHOT"
)

bootSnippet := "ScalaJSExample().main(document.getElementById('content'));"

updateBrowsers <<= updateBrowsers.triggeredBy(fastOptJS in Compile)


