import x.utils.StrUtils

/**
  * Created by xw on 2019/8/2.
  */
object StrUtilsTest {

  def main(args: Array[String]): Unit = {
    println(StrUtils.toMD5("你好"))
    println(StrUtils.upperCaseFirstLetter("sdf你好"))
    println(StrUtils.upperCaseFirstLetter("Sdf你好"))
    println(StrUtils.lowerCaseFirstLetter("sdf你好"))
    println(StrUtils.lowerCaseFirstLetter("Sdf你好"))
    println(StrUtils.underLineToCamel("read_me_你好"))
    println(StrUtils.camelToUnderLine("readMe你好"))
  }
}
