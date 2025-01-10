package webapp

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object LoadCss {

  @js.native
  @JSImport("../../../../src/main/css/index.css", JSImport.Namespace)
  object Css extends js.Object

  //
  @js.native
  @JSImport("../../../../src/main/css/tailwind.css", JSImport.Namespace)
  object TailwindCss extends js.Object

  def apply(): Unit = {

    // keep these references even though IntelliJ thinks they're not used

    // noinspection ScalaUnusedExpression
    TailwindCss: Unit
    // noinspection ScalaUnusedExpression
    Css: Unit // load css
    ()
  }
}
