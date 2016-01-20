package example

import rx._
import Framework._
import DemoImg.MediaPathLayout
import org.scalajs.dom

import scala.scalajs.js
import scalatags.JsDom.all._

object Droppable {

  def zoomcrop(path: String, divCls: String = "") = {
    div(cls:=s"zc $divCls", style:=s"background-image: url($path); height: 200px")
  }
  def droppable(layout: MediaPathLayout)(implicit owner: Ctx.Owner): HtmlTag = {
    val somenum = scala.util.Random.nextInt()

    // possible rx leak :(
    val formy = form(cls:="image-select", action:="")(
      layout.fileInput,
      i(cls:="fa fa-camera fa-2x image-select__icon"),
      div(cls:="image-select__message"),
      Rx {
        zoomcrop(layout.current().get.path, "zc--square")
      }
    ).render

    formy.ondragover = fileHover
    formy.ondragleave = fileHover
    formy.ondrop = fileSelect
    println(layout)
    println(layout.fileInput)
    println(layout.fileInput.onchange)
    layout.fileInput.onchange = { (ev: dom.Event) =>
      println(ev)
      ev.stopPropagation()
      ev.preventDefault()
      layout.filez() = Option(layout.fileInput.files(0))
      println("OMG LAYOUT!")
      println(layout.current)
      layout.current.recalc()
    }

    def fileHover: js.Function1[dom.DragEvent, _] = (ev: dom.DragEvent) => {
      ev.stopPropagation()
      ev.preventDefault()
      if(ev.`type` == "dragover") {
        formy.classList.add("file-hover")
      } else {
        formy.classList.remove("file-hover")
      }
    }

    def fileSelect: js.Function1[dom.DragEvent, _] = (ev: dom.DragEvent) => {
      ev.stopPropagation()
      ev.preventDefault()
      fileHover(ev)
      layout.filez() = Option(ev.dataTransfer.files(0))
    }

    label(formy)
  }
}