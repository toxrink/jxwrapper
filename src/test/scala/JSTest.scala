import x.js.JsWrapper

/**
  * Created by xw on 2019/8/1.
  */
object JSTest {
  def main(args: Array[String]): Unit = {
    val path = "C:\\Users\\admin\\Desktop\\a.js"
    val m = Map("name" -> "jack")
    val func = JsWrapper.loadJs[Map[String, String]](path, "parse")
    println(func.call(m))
  }
}
