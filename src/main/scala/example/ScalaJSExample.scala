package example

import likelib.StringTryLike
import scala.util._
import rx._
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import scalatags.JsDom.all._
import formrx._
import formrx.implicits.all._
import framework.Framework._

object Demo1 {
  case class UserPass(firstname: String, pass : String)

  //The form layout
  class UserPassLayout(implicit ctx: Ctx.Owner) {
    val firstname = input(`type`:="text").render
    val pass = input(`type`:="password").render
  }

  //The form instance
  val loginForm = FormRx[UserPass,UserPassLayout]

  val default = UserPass("Bob!","supersecretbob")

  //Example, unused
  import scalatags.JsDom.all._
  val loginTag: HtmlTag =
    form(
      loginForm.firstname,
      loginForm.pass
    )(
      input(`type`:="Submit"),
      onsubmit := {() =>
      loginForm.current.now match {
        case Success(usrPass) => println(s"$usrPass")
        case Failure(err) => println(s"FAILED!: ${err.getMessage}")
      }
      false
    }
  )
}

object Demo2 {
  case class Inner(foo: String, bar: Int)

  case class Nested(top: String, inner: Inner, other: Inner)
}

object Demo3 {
  sealed trait Color
  case object Red extends Color
  case object Green extends Color
  case object Blue extends Color

  case class FakeId(id: Long)

  case class Info(fid: FakeId, doit: Boolean, title: String, colors: Set[Color])
}

object Demo4 {
  //Example of typesafe values that can be used on both client and server (eg if this is defined in "shared")
  case class OnlyA private (value: String) extends AnyVal

  object OnlyA {
    private def valid(inp: String): Boolean = { inp.forall(_ == 'A') }
    implicit val like = likelib.validate[String,OnlyA] { str =>
      if(valid(str)) { Success(new OnlyA(str)) }
      else Failure(new IllegalArgumentException("Requries only A!"))
    }
  }

  case class Size5 private (value: String) extends AnyVal

  object Size5 {
    implicit val like = likelib.validate[String,Size5] { str =>
      if (str.length == 5) Success(new Size5(str))
      else Failure(new IllegalArgumentException("Requires exactly 5 characters!"))
    }
  }

  case class Example(a: OnlyA, b: Size5, c: Int)
}

object Demo5 {
  sealed trait SkillLevel
  case object Average extends SkillLevel
  case object Intermediate extends SkillLevel
  case object Expert extends SkillLevel

  case class Skill(name: String, level: SkillLevel)

  case class Profile(foo: String, bar: Int, skills: List[Skill])
}

object DemoImg {
  import scalajs.js.Dynamic.{global => g}

  case class MediaPath(path: String)

  class MediaPathLayout()(implicit ctx: Ctx.Owner) extends FormRx[MediaPath] {
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

  class GameLayout(implicit ctx: Ctx.Owner) {
    val id = input(`type`:="text").render
    val title = input(`type`:="text").render
    val img = new MediaPathLayout()(Ctx.Owner.Unsafe)
  }
}


object Sparkle {
  def row: HtmlTag = div(cls:="row")
  def row(classes: String): HtmlTag = div(cls:=s"row $classes")

  def column(classes: String): HtmlTag = div(cls:=s"column $classes")

  def sparkle[T](labelTxt: String, field: Validate[T])(implicit owner: Ctx.Owner) = {
    val successColor = "#3c763d"
    val failedColor = "#a94442"
    val normalColor = "#4d4d4d"

    def colorize = {
      color := field.current.map {
        case Success(_) => successColor
        case Failure(FormidableUninitialized) => normalColor
        case _ => failedColor
      }
    }

    def icon = {
      field.current.map {
        case Success(_) => span(cls:="fa fa-check postfix", color := successColor)
        case Failure(FormidableUninitialized) => span(cls:="fa fa-beer postfix", color := normalColor)
        case _ => span(cls:="fa fa-close postfix", color := failedColor)
      }
    }

    def labelrx = {
      field.current.map {
        case Failure(FormidableUninitialized) => labelTxt
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
      (formidable: FormRx[T], defaultTxt: String, default: T)
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

  object Demo2Layouts {
    import Demo2._
    class InnerLayout(implicit ctx: Ctx.Owner) {
      val foo = input(`type`:="text").render
      val bar = SelectionRx[Int]()(
        Opt(1)(value:="One","One"),
        Opt(2)(value:="Two","twwo"),
        Opt(42)(value:="Life","What is?"),
        Opt(5)(value:="Five","5ive")
      )
    }
    class NestedLayout(implicit ctx: Ctx.Owner) {
      val top = input(`type`:="text").render
      val inner = FormRx[Inner,InnerLayout]
      val other = FormRx[Inner,InnerLayout]
    }
  }

  def second(implicit ctx: Ctx.Owner): HtmlTag = {
    import Demo2._
    import Demo2Layouts._

    val nestedForm = FormRx[Nested,NestedLayout]
    val default = Nested("This is top", Inner("This is foo",2),Inner("Other foo",5))
    template("Example 2", "Formidable can nest")(nestedForm,"Default",default) {
      form(
        nestedForm.top,
        label("Inner:"),
        nestedForm.inner.foo,
        nestedForm.inner.bar.select,
        label("Other:"),
        nestedForm.other.foo,
        nestedForm.other.bar.select
      )
    }
  }

  object Demo2Alternative {
    import Demo2._
    class InnerLayout(implicit ctx: Ctx.Owner) {
      val foo = input(`type`:="text").render
      val bar = Var(0)
    }
    class NestedLayout(implicit ctx: Ctx.Owner) {
      val top = input(`type`:="text").render
      val inner = FormRx[Inner,InnerLayout]
      val other = FormRx[Inner,InnerLayout]
    }
  }

  def secondAlt(implicit ctx: Ctx.Owner): HtmlTag = {
    import Demo2._
    import Demo2Alternative._

    def buttons(inp: Var[Int]): Rx[HtmlTag] = Rx {
      div(
        label("Current Value: " + inp()),
        ul(cls:="button-group")(
          li(a(cls:="button", onclick:={ () => inp() = inp.now + 1 })("Inc")),
          li(a(cls:="button", onclick:={ () => inp() = inp.now - 1 })("Dec"))
        )
      )
    }

    val nestedForm = FormRx[Nested,NestedLayout]
    val default = Nested("This is top", Inner("This is foo",2),Inner("Other foo",5))
    template("Example 2a", "Formidable can nest")(nestedForm,"Default",default) {
      form(
        nestedForm.top,
        label("Inner:"),
        nestedForm.inner.foo,
        buttons(nestedForm.inner.bar),
        label("Other:"),
        nestedForm.other.foo,
        buttons(nestedForm.other.bar)
      )
    }
  }

  def third(implicit ctx: Ctx.Owner): HtmlTag = {
    import Demo3._

    class InfoLayout(implicit ctx: Ctx.Owner) {
      val fid = Ignored(FakeId(-1))(Ctx.Owner.Unsafe)
      val doit = CheckboxRx.bool(false)(Ctx.Owner.Unsafe)
      val title = input(`type`:="text").render
      val colors = CheckboxRx.set[Color]("color")(
        Chk(Red)(value:="Red"),
        Chk(Green)(value:="Grn"),
        Chk(Blue)(value:="Blue")
      )
    }

    val form3 = FormRx[Info,InfoLayout]
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
    import Sparkle._
    class LayoutExample(implicit ctx: Ctx.Owner) {
      val a = InputRx.validate[OnlyA](true)(placeholder:="a")
      val b = InputRx.validate[Size5](true)(placeholder:="b")
      val c = InputRx.validate[Int](true)(placeholder:="c")
    }

    val form4 = FormRx[Example,LayoutExample]
    val default = Example(OnlyA.like.from("AAA").get, Size5.like.from("12345").get, 42)
    template("Example 4", "Basic Validating fields")(form4,"Default",default) {
      form(
        sparkle("My A Field",form4.a),
        sparkle("My B Field",form4.b),
        sparkle("My C Field",form4.c)
      )
    }
  }

  object fifth {
    import Demo5._

    class SkillLayout(implicit ctx: Ctx.Owner) {
      val name = input(`type`:="text").render
      val level = SelectionRx[SkillLevel]()(
        Opt(Average)("Average"),
        Opt(Intermediate)("Intermediate"),
        Opt(Expert)("Expert")
      )
    }

    class ProfileLayout(implicit ctx: Ctx.Owner) {
      val foo = input(`type`:="text").render
      val bar = Var(-1)

      //Skills List
      def newSkill(txt: String): Skill = Skill(txt,Average)

      val skills = InputRx
        .list(input(`type`:="text", placeholder:="New Skill*"))(newSkill)(() => FormRx[Skill,SkillLayout])
    }

    val default = Profile("Foo!",42,List(
      Skill("numchuku", Average),
      Skill("bow hunting", Intermediate),
      Skill("computer hacking", Expert)
    ))

    val profileForm = FormRx[Profile,ProfileLayout]

    def skillTag(skill: Skill): HtmlTag = {
      val clsTxt = skill.level match {
        case Average => "round label success"
        case Intermediate => "round label warning"
        case Expert => "round label alert"
      }
      li(marginRight:=10.px,cls:=clsTxt,skill.name)
    }

    val skillsTag: Rx[HtmlTag] = profileForm.skills.current.filter(_.isSuccess).map(_.get).map { skills =>
      ul(cls:="padLeft", skills.map(skillTag))
    }

    val outputTag = template("Example 5", "A List example")(profileForm,"Default",default) {
      form(
        label("Foo:"),
        profileForm.foo,
        label("Skills:"),
        profileForm.skills.input,
        skillsTag,
        Rx {
          val skills = profileForm.skills.values()
          div(profileForm.skills.values().zipWithIndex.map { case (skillForm,idx) =>
            ul(cls:="button-group")(
              li(skillForm.name),
              li(skillForm.level.select),
              li(a(paddingLeft:=10.px))(
                onclick:={ () =>
                  skills.remove(idx)
                  profileForm.skills.values.propagate()
                }
              )(raw("&times"))
            )
          })
        }
      )
    }
  }
  
  val imgRx: HtmlTag = {
    import DemoImg._
    val imgForm  = FormRx[Game,GameLayout]
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

  val first = template("Example 1","Basic User/Password form")(Demo1.loginForm,"Bob",Demo1.default) {
    form(
      Demo1.loginForm.firstname,
      Demo1.loginForm.pass
    )
  }

  @JSExport
  def main(content: dom.html.Div): Unit = {
    import Ctx.Owner.Unsafe._
    content.innerHTML = ""
    content.appendChild(row(column("small-12 text-center")(h1("Formidable"))).render)
    content.appendChild(Seq(first,hr).render)
    content.appendChild(Seq(second,hr).render)
    content.appendChild(Seq(secondAlt,hr).render)
    content.appendChild(Seq(third,hr).render)
    content.appendChild(Seq(fourth,hr).render)
    content.appendChild(Seq(fifth.outputTag,hr).render)
    //content.appendChild(Seq(imgRx,hr).render)
  }
}
