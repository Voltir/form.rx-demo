import com.lihaoyi.workbench.Plugin._

enablePlugins(ScalaJSPlugin)

workbenchSettings
//URL: http://localhost:12345/target/scala-2.11/classes/index-dev.html

name := "Form.rx Demo"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.0",
  "com.lihaoyi" %%% "scalarx" % "0.3.1",
  "com.lihaoyi" %%% "scalatags" % "0.5.4",
  "com.stabletechs" %%% "likelib" % "0.1.1",
  "com.stabletechs" %%% "frameworkrx" % "0.1.0",
  "com.stabletechs" %%% "formrx" % "1.1.0"
)

bootSnippet := "ScalaJSExample().main(document.getElementById('content'));"

updateBrowsers <<= updateBrowsers.triggeredBy(fastOptJS in Compile)


