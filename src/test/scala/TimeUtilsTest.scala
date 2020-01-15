import x.utils.TimeUtils

/**
  * Created by xw on 2020/1/15.
  */
object TimeUtilsTest {
  def main(args: Array[String]): Unit = {
    val now = TimeUtils.getNow
    println(now)
    println(TimeUtils.format1(now))
    println(TimeUtils.format2(now))
    println(TimeUtils.getDays(TimeUtils.getNowBeforeByDay(10), TimeUtils.getNow))
    println(TimeUtils.getFirstDayOfBeforeMonth)
    println(TimeUtils.getFirstDayOfMonth)
    println(TimeUtils.getFirstDayOfWeek)
    println(TimeUtils.getMillisecond(TimeUtils.getNowBeforeByDay(1000), TimeUtils.getNow))
    println(TimeUtils.getTimestamp)
    println(TimeUtils.parese1("20200115171502"))
    println(TimeUtils.parese2("2020-01-15 17:15:02"))
  }
}
