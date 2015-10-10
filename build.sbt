import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

import com.lihaoyi.workbench.Plugin._

workbenchSettings

lazy val root = project.in(file(".")).enablePlugins(ScalaJSPlugin)

name := "Formidable Demo"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

//resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.8.2",
  "com.lihaoyi" %%% "scalarx" % "0.2.9-SNAPSHOT",
  "com.lihaoyi" %%% "scalatags" % "0.5.2",
  "com.stabletech" %%% "formidable" % "0.0.10-SNAPSHOT"
)

bootSnippet := "ScalaJSExample().main(document.getElementById('content'));"

//updateBrowsers <<= updateBrowsers.triggeredBy(ScalaJSKeys.fastOptJS in Compile)

