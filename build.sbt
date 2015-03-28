<<<<<<< HEAD
import com.lihaoyi.workbench.Plugin._

enablePlugins(ScalaJSPlugin)
=======
import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

//import com.lihaoyi.workbench.Plugin._
>>>>>>> 20c4bec4c9ddf73215f8159736f773bd46a348e9

//workbenchSettings

lazy val root = project.in(file(".")).enablePlugins(ScalaJSPlugin)

name := "Formidable Demo"

version := "0.1.0-SNAPSHOT"

<<<<<<< HEAD
scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.8.0",
  "com.lihaoyi" %%% "scalarx" % "0.2.8",
  "com.lihaoyi" %%% "scalatags" % "0.5.0",
  "com.stabletech" %%% "formidable" % "0.0.4-SNAPSHOT"
=======
scalaVersion := "2.11.5"

//resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "org.scala-js" %%%! "scalajs-dom" % "0.7.0",
  "com.lihaoyi" %%%! "scalarx" % "0.2.7-RC1",
  "com.lihaoyi" %%%! "scalatags" % "0.4.3-RC1",
  "com.stabletech" %%%! "formidable" % "0.0.2-SNAPSHOT"
>>>>>>> 20c4bec4c9ddf73215f8159736f773bd46a348e9
)

//bootSnippet := "ScalaJSExample().main(document.getElementById('content'));"

<<<<<<< HEAD
updateBrowsers <<= updateBrowsers.triggeredBy(fastOptJS in Compile)
=======
//updateBrowsers <<= updateBrowsers.triggeredBy(ScalaJSKeys.fastOptJS in Compile)
>>>>>>> 20c4bec4c9ddf73215f8159736f773bd46a348e9

