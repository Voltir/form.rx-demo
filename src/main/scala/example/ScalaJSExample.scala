package example

import likelib.StringTryLike
import scala.util._
import rx._
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import scalatags.JsDom.all._
import formidable._
import formidable.implicits.all._
import Framework._

object Demo1 {
  case class UserPass(firstname: String, pass : String)

  //The form layout
  class UserPassLayout(implicit ctx: Ctx.Owner) extends LayoutFor[UserPass] {
    val firstname = input(`type`:="text").render
    val pass = input(`type`:="password").render
  }

  //The form instance
  val form = FormidableRx.apply2[UserPass,UserPassLayout]

  //The Html
  val default = UserPass("Bob!","supersecretbob")

}

object Demo2 {
  case class User(id: String) extends AnyVal
  object User {
    implicit val like = likelib.like[String,User]
  }
  case class Inner(foo: String, bar: Int)
  case class Nested(top: String, uid: User, inner: Inner, other: Inner)
  trait InnerLayout {
    val foo = input(`type`:="text").render
    val bar = SelectionRx[Int]()(
      Opt(1)(value:="One","One"),
      Opt(2)(value:="Two","twwo"),
      Opt(42)(value:="Life","Fizzle"),
      Opt(5)(value:="Five","5ive")
    )(Ctx.Owner.Unsafe)
  }
  trait NestedLayout {
    val top = input(`type`:="text").render
    val uid = input(`type`:="text").render
    val inner = FormidableRx[InnerLayout,Inner]
    val other = FormidableRx[InnerLayout,Inner]
  }
}

object Demo3 {

  sealed trait Color
  case object Red extends Color
  case object Green extends Color
  case object Blue extends Color

  case class FakeId(id: Long)

  case class Info(fid: FakeId, doit: Boolean, title: String, colors: Set[Color])

  trait InfoLayout {
    val fid = Ignored(FakeId(-1))(Ctx.Owner.Unsafe)
    val doit = CheckboxRx.bool(false)(Ctx.Owner.Unsafe)
    val title = input(`type`:="text").render
    val colors = CheckboxRx.set[Color]("color")(
      Chk(Red)(value:="Red"),
      Chk(Green)(value:="Grn"),
      Chk(Blue)(value:="Blue")
    )(Ctx.Owner.Unsafe)
  }
}

object Demo4 {
  //Example of a Typesafe values that can be used on both client and server
  case class OnlyA private (value: String) extends AnyVal

  object OnlyA {

    private def valid(inp: String): Boolean = { inp.forall(_ == 'A') }

    implicit val like: StringTryLike[OnlyA] = new StringTryLike[OnlyA] {
      override def to(inp: OnlyA) = inp.value
      override def from(str: String): Try[OnlyA] = {
        if(valid(str)) { Success(new OnlyA(str)) }
        else Failure(new IllegalArgumentException("Requries only A!"))
      }
    }
  }



  case class Size5 private (value: String) extends AnyVal

  object Size5 {
    implicit val like: StringTryLike[Size5] = new StringTryLike[Size5] {
      override def to(inp: Size5) = inp.value
      override def from(str: String): Try[Size5] = {
        if (str.length == 5) Success(new Size5(str))
        else Failure(new IllegalArgumentException("Requires exactly 5 characters!"))
      }
    }
  }

  case class Example(a: OnlyA, b: Size5, c: Int)
}

object DemoImg {
  import scalajs.js.Dynamic.{global => g}

  case class MediaPath(path: String)

  class MediaPathLayout()(implicit ctx: Ctx.Owner) extends FormidableRx[MediaPath] {
    val path: Var[String] = Var("")
    val filez: Var[Option[dom.File]] = Var(None)

    val fileInput: dom.html.Input =
      input(`type`:="file",
        accept:="image/*"
        //onchange:={ () => path() = fileInput.value }
      ).render

    private def fizzlepop(file: dom.File): String = {
      g.URL.createObjectURL(file).asInstanceOf[String]
    }

    override val current: Rx[Try[MediaPath]] = Rx{Try{MediaPath(
      if(filez().isDefined) fizzlepop(filez.now.get) else path()
    )}}

    def set(inp: MediaPath) = {
      filez() = None
      path() = inp.path
    }

    override def reset() = {
      path() = ""
      filez() = None
    }
  }

  case class Game(id: String, title: String, img: MediaPath)

  trait GameLayout {
    val id = input(`type`:="text").render
    val title = input(`type`:="text").render
    val img = new MediaPathLayout()(Ctx.Owner.Unsafe)
  }
}


object todosparkle {
  def row: HtmlTag = div(cls:="row")
  def row(classes: String): HtmlTag = div(cls:=s"row $classes")

  def column(classes: String): HtmlTag = div(cls:=s"column $classes")

  def sparkle[T](labelTxt: String, field: Validate[T])(implicit owner: Ctx.Owner) = {
    val successColor = "#3c763d"
    val failedColor = "#a94442"
    val normalColor = "#4d4d4d"

    def colorize = {
      color := field.current.map {
        case Success(_)           => successColor
        case Failure(FormidableUninitialized) => normalColor
        case _                    => failedColor
      }
    }

    def icon = {
      field.current.map {
        case Success(_)           => span(cls:="fa fa-check postfix", color := successColor)
        case Failure(FormidableUninitialized) => span(cls:="fa fa-beer postfix", color := normalColor)
        case _                    => span(cls:="fa fa-close postfix", color := failedColor)
      }
    }

    def labelrx = {
      field.current.map {
        case Failure(FormidableUninitialized) => { labelTxt }
        case Failure(err) => s"$labelTxt (${err.getMessage})"
        case Success(_) => labelTxt
      }
    }

    row("collapse")(
      label(labelrx,colorize),
      column("small-9 large-11")(field.input),
      column("small-3 large-1")(icon)
    ).render
  }
}

@JSExport
object ScalaJSExample {

  def row: HtmlTag = div(cls:="row")
  def row(classes: String): HtmlTag = div(cls:=s"row $classes")

  def column(classes: String): HtmlTag = div(cls:=s"column $classes")

  def template[T]
      (title: String, description: String)
      (formidable: FormidableRx[T], defaultTxt: String, default: T)
      (formTag: HtmlTag): HtmlTag = {
    val created = div("Not created yet").render
    row(
      column("small-3")(
        row(column("small-12")(h3(title))),
        row(column("small-12")(description)),
        row(column("small-12")("Auto fill with ")(a(
          href:="javascript:void(0)",
          defaultTxt,
          onclick := {() => formidable.set(default)}
        ))),
        row(column("small-12")(created))
      ),
      column("small-9")(
        formTag(
          input(`type`:="Submit"),
          onsubmit := {() =>
            formidable.current.now match {
              case Success(thing) => created.innerHTML = s"$thing"
              case Failure(err) => created.innerHTML = s"FAILED!: ${err.getMessage}"
            }
            false
          }
        )
      )
    )
  }

  def second: HtmlTag = {
    import Demo2._

    trait InnerLayout {
      val foo = input(`type`:="text").render
      val bar = SelectionRx[Int]()(
        Opt(1)(value:="One","One"),
        Opt(2)(value:="Two","twwo"),
        Opt(42)(value:="Life","Fizzle"),
        Opt(5)(value:="Five","5ive")
      )(Ctx.Owner.Unsafe)
    }
    trait NestedLayout {
      val top = input(`type`:="text").render
      val uid = input(`type`:="text").render
      val inner = FormidableRx[InnerLayout,Inner]
      val other = FormidableRx[InnerLayout,Inner]
    }

    val form2 = FormidableRx[NestedLayout,Nested]
    val default = Nested("This is top",User("fiz@foo.com"),Inner("This is foo",2),Inner("Other foo",5))
    template("Example 2", "Formidable can nest")(form2,"Default",default) {
      form(
        form2.top,
        form2.uid,
        label("Inner:"),
        form2.inner.foo,
        form2.inner.bar.select,
        label("Other:"),
        form2.other.foo,
        form2.other.bar.select
      )
    }
  }

  def third: HtmlTag = {
    import Demo3._

    trait InfoLayout {
      val fid = Ignored(FakeId(-1))(Ctx.Owner.Unsafe)
      val doit = CheckboxRx.bool(false)(Ctx.Owner.Unsafe)
      val title = input(`type`:="text").render
      val colors = CheckboxRx.set[Color]("color")(
        Chk(Red)(value:="Red"),
        Chk(Green)(value:="Grn"),
        Chk(Blue)(value:="Blue")
      )(Ctx.Owner.Unsafe)
    }

    val form3 = FormidableRx[InfoLayout,Info]
    val default = Info(FakeId(-1),true,"My Color Choices",Set(Red,Green))
    template("Example 3", "Example with checkboxes")(form3,"Default",default) {
      form(
        label(form3.doit.input,"Do it?"),
        form3.title,
        form3.colors.checkboxes.map(c => label(c.input,c.input.value))
      )
    }
  }

  val fourth: HtmlTag = {
    import Demo4._
    import todosparkle._
    implicit val ctx = Ctx.Owner.Unsafe
    trait LayoutExample {
      val a = InputRx.validate[OnlyA](true)(placeholder:="a")
      val b = InputRx.validate[Size5](true)(placeholder:="b")
      val c = InputRx.validate[Int](true)(placeholder:="c")
    }

    val form4 = FormidableRx[LayoutExample, Example]
    val default = Example(OnlyA.like.from("AAA").get, Size5.like.from("12345").get, 42)
    template("Example 4", "Basic Validating fields")(form4,"Default",default) {
      form(
        sparkle("My A Field",form4.a),
        sparkle("My B Field",form4.b),
        sparkle("My C Field",form4.c)
      )
    }
  }


  val imgRx: HtmlTag = {
    import DemoImg._
    val imgForm  = FormidableRx[GameLayout,Game]
    val default = Game("GAMEID","OMG YOLO",MediaPath("http://placekitten.com/350/350"))
    template("Example Img Upload","Basic User/Password form")(imgForm,"Wurt",default) {
      val gameMediaSection = {
        div(
          imgForm.id,
          imgForm.title,
          Droppable.droppable(imgForm.img),
          Rx {
            imgForm.img.filez().map{ _ =>
              div("grid--collapse")(
                div()(
                  a(cls:="button small secondary expand", onclick:={ () => imgForm.img.filez() = None})(
                    i(cls:="fa fa-times fa-lg"),
                    " Discard"
                  )
                ),
                div()(
                  div(cls:=imgForm.current.map{ g => s"button small expand ${if(g.isSuccess) "" else "disabled"}"},
                    onclick:={ () => println("Test")
                    })(
                      i(cls:="fa fa-check fa-lg"),
                      " Upload"
                    )
                )
              )
            }.getOrElse{
              p(cls:="form-hint")("No Files..")
            }
          }
        )
      }
      gameMediaSection
    }
  }


//  val demo1 = template("Example 1","Basic User/Password form")(Demo1.form,"Bob",Demo1.default) {
//    form(
//      Demo1.form.firstname,
//      Demo1.form.pass
//    )
//  }

  @JSExport
  def main(content: dom.html.Div): Unit = {
    content.innerHTML = ""
    content.appendChild(row(column("small-12 text-center")(h1("Formidable"))).render)
    //content.appendChild(Seq(demo1,hr).render)
    content.appendChild(Seq(second,hr).render)
    content.appendChild(Seq(third,hr).render)
    content.appendChild(Seq(fourth,hr).render)
    content.appendChild(Seq(imgRx,hr).render)
  }
}
