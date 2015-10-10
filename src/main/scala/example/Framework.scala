package example

import scalatags.JsDom.all._
import scala.util.{Failure, Success}
import rx._
import org.scalajs.dom.Element

object Framework {

  /**
   * Wraps reactive strings in spans, so they can be referenced/replaced
   * when the Rx changes.
   */
  implicit def RxStr[T](r: Rx[T])(implicit f: T => Modifier): Modifier = {
    rxMod(Rx(span(r())))
  }

  /**
   * Sticks some Rx into a Scalatags fragment, which means hooking up an Obs
   * to propagate changes into the DOM via the element's ID. Monkey-patches
   * the Obs onto the element itself so we have a reference to kill it when
   * the element leaves the DOM (e.g. it gets deleted).
   */
  implicit def rxMod[Tag <: HtmlTag](r: Rx[Tag]): Modifier = {
    def rSafe = r.toTry match {
      case Success(v) => v.render
      case Failure(e) => span(e.toString, backgroundColor := "red").render
    }
    var last = rSafe
    //Obs(r, skipInitial = true){
    r.foreach { _ =>
      val newLast = rSafe
      if(last.parentElement != null) {
        last.parentElement.replaceChild(newLast, last)
        last = newLast
      }
    }
    bindNode(last)
  }
  implicit def RxAttrValue[T: AttrValue] = new AttrValue[Rx[T]]{
    def apply(t: Element, a: Attr, r: Rx[T]): Unit = {
      //Obs(r){ implicitly[AttrValue[T]].apply(t, a, r.now)}
      r.foreach { _ => implicitly[AttrValue[T]].apply(t, a, r.now) }
    }
  }
  implicit def RxStyleValue[T: StyleValue] = new StyleValue[Rx[T]]{
    def apply(t: Element, s: Style, r: Rx[T]): Unit = {
      //Obs(r){ implicitly[StyleValue[T]].apply(t, s, r.now)}
      r.foreach { _ => implicitly[StyleValue[T]].apply(t, s, r.now) }
    }
  }
}
