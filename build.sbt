import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

//import com.lihaoyi.workbench.Plugin._

//workbenchSettings

lazy val root = project.in(file(".")).enablePlugins(ScalaJSPlugin)

name := "Formidable Demo"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.5"

//resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "org.scala-js" %%%! "scalajs-dom" % "0.7.0",
  "com.lihaoyi" %%%! "scalarx" % "0.2.7-RC1",
  "com.lihaoyi" %%%! "scalatags" % "0.4.3-RC1",
  "com.stabletech" %%%! "formidable" % "0.0.2-SNAPSHOT"
)

//bootSnippet := "ScalaJSExample().main(document.getElementById('content'));"

//updateBrowsers <<= updateBrowsers.triggeredBy(ScalaJSKeys.fastOptJS in Compile)

