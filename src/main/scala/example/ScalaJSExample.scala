package example

import org.scalajs.dom.HTMLInputElement
import scala.util._
import rx._
import rx.ops._
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import scalatags.JsDom.all._
import formidable._
import formidable.Implicits._
import Framework._

object Demo1 {

  case class UserPass(name: String, password: String)

  trait UserPassLayout {
    val name = input(`type`:="text").render
    val password = input(`type`:="password").render
  }
}

object Demo2 {
  case class Inner(foo: String, bar: Int)
  case class Nested(top: String, inner: Inner, other: Inner)

  trait InnerLayout {
    val foo = input(`type`:="text").render
    val bar = SelectionOf[Int](
      Opt(1)(value:="One","One"),
      Opt(2)(value:="Two","twwo"),
      Opt(42)(value:="Life","Fizzle"),
      Opt(5)(value:="Five","5ive")
    )
  }
  trait NestedLayout {
    val top = input(`type`:="text").render
    val inner = Formidable[InnerLayout,Inner]
    val other = Formidable[InnerLayout,Inner]
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
    val fid = Ignored(FakeId(-1))
    val doit = CheckboxBool()
    val title = input(`type`:="text").render
    val colors = CheckboxSet[Color]("color")(
      Chk(Red)(value:="Red"),
      Chk(Green)(value:="Grn"),
      Chk(Blue)(value:="Blue")
    )
  }
}

object Demo4 {

  case object Unitialized extends Throwable("Unitialized Field")

  //Example of a Typesafe values that can be used on both client and server
  case class OnlyA private (value: String) extends AnyVal

  object OnlyA {
    private def valid(inp: String): Boolean = { inp.forall(_ == 'A') }
    def fromString(str: String): Try[OnlyA] = {
      if(valid(str)) { Success(new OnlyA(str)) }
      else Failure(new IllegalArgumentException("Requries only A!"))
    }
  }

  case class Size5 private (value: String) extends AnyVal

  object Size5 {
    def fromString(str: String): Try[Size5] = {
      if(str.length == 5) { Success(new Size5(str)) }
      else Failure(new IllegalArgumentException("Requires exactly 5 characters!"))
    }
  }

  case class Example(a: OnlyA, b: Size5, c: Int)

  import scala.util.{Try,Success,Failure}
  import rx._

  class Validate[T]
    (check: String => Try[T], asString: T => String, mods: Modifier*)
    (rxMods: (Var[Try[T]] => Modifier)*) extends Formidable[T] {

    val current: Var[Try[T]] = Var(Failure(Unitialized))

    lazy val input: HTMLInputElement = scalatags.JsDom.all.input(
      `type`:="text",
      onkeyup := { () => current() = check(input.value) },
      mods,
      rxMods.map(_(current))
    ).render

    override def build(): Try[T] = current()
    override def unbuild(inp: T) = {
      current() = Success(inp)
      input.value = asString(inp)
    }
  }

  object Validate {
    def apply[T](check: String => Try[T], zzz: T => String,mods: Modifier*)(rxMods: (Var[Try[T]] => Modifier)*)  = new Validate(check,zzz,mods:_*)(rxMods:_*)
  }

  def withClasses[T](valid: String, invalid: String) = (v:Var[Try[T]]) => cls := v.map { t => if(t.isSuccess) valid else invalid }

  trait LayoutExample {
    val a = Validate[OnlyA](OnlyA.fromString,_.value)(
      c => backgroundColor := c.map { t => if(t.isSuccess) "green" else "red" },
      withClasses("valid","invalid")
    )
    val b = Validate[Size5](Size5.fromString,_.value)(withClasses("valid","invalid"))
    val c = input(`type`:="text").render
  }
}

object Demo5 {
  case class ValidationDemo(
    firstName: String,
    lastName: String,
    userName: String,
    emailAddress: String,
    website: String,
    age: Int,
    bio: String
  )

  trait ValidationLayout {

  }
}

@JSExport
object ScalaJSExample {

  def row: HtmlTag = div(cls:="row")

  def column(classes: String): HtmlTag = div(cls:=s"column $classes")

  def template[T]
  (title: String, description: String)
  (formidable: Formidable[T], defaultTxt: String, default: T)
  (formTag: HtmlTag): HtmlTag = {
    val created = div("Not created yet").render
    row(
      column("small-3")(
        row(column("small-12")(h3(title))),
        row(column("small-12")(description)),
        row(column("small-12")("Auto fill with ")(a(
          href:="javascript:void(0)",
          defaultTxt,
          onclick := {() => formidable.unbuild(default)}
        ))),
        row(column("small-12")(created))
      ),
      column("small-9")(
        formTag(
          input(`type`:="Submit"),
          onsubmit := {() =>
            formidable.build() match {
              case Success(thing) => created.innerHTML = s"$thing"
              case Failure(err) => created.innerHTML = s"FAILED!: ${err.getMessage}"
            }
            false
          }
        )
      )
    )
  }

  def first: HtmlTag = {
    val form1 = Formidable[Demo1.UserPassLayout,Demo1.UserPass]
    val default = Demo1.UserPass("Bob!","supersecretbob")
    template("Example 1","Basic User/Password form")(form1,"Bob",default) {
      form(
        form1.name,
        form1.password
      )
    }
  }

  def second: HtmlTag = {
    import Demo2._
    val form2 = Formidable[NestedLayout,Nested]
    val default = Nested("This is top",Inner("This is foo",2),Inner("Other foo",5))
    template("Example 2", "Formidable can nest")(form2,"Default",default) {
      form(
        form2.top,
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
    val form3 = Formidable[InfoLayout,Info]
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
    import scala.util.{Try,Success,Failure}
    val form4 = Formidable[LayoutExample,Example]
    val default = Example(OnlyA.fromString("AAA").get,Size5.fromString("12345").get,42)

    def validatingTag[T](field: Validate[T]) = {
      div(
        field.input,
        Rx {
          field.current() match {
            case Failure(Unitialized) => span
            case Failure(err) => small(err.getMessage)
            case Success(_) => span
          }
        }
      ).render
    }

    template("Example 4", "Basic Validating fields")(form4,"Default",default) {
      form(
        validatingTag(form4.a),
        validatingTag(form4.b),
        form4.c
      )
    }
  }

  @JSExport
  def main(content: dom.HTMLDivElement): Unit = {
    content.innerHTML = ""
    content.appendChild(row(column("small-12 text-center")(h1("Example Forms"))).render)
    content.appendChild(Seq(first,hr).render)
    content.appendChild(Seq(second,hr).render)
    content.appendChild(Seq(third,hr).render)
    content.appendChild(Seq(fourth,hr).render)
  }
}
