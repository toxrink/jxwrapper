import x.utils.TimeUtils

/**
  * Created by xw on 2020/1/15.
  */
object TimeUtilsTest {
  def main(args: Array[String]): Unit = {
    val now = TimeUtils.getNow

    println(TimeUtils.format1(now))
    println(TimeUtils.format2(now))
  }
}
