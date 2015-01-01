import scala.scalajs.sbtplugin.ScalaJSPlugin._
import com.lihaoyi.workbench.Plugin._

scalaJSSettings

workbenchSettings

name := "Formidable Demo"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6",
  "com.scalarx" %%% "scalarx" % "0.2.5",
  "com.scalatags" %%% "scalatags" % "0.4.2",
  "com.stabletech" %%% "formidable" % "0.0.1-SNAPSHOT"
)

bootSnippet := "ScalaJSExample().main(document.getElementById('content'));"

updateBrowsers <<= updateBrowsers.triggeredBy(ScalaJSKeys.fastOptJS in Compile)

