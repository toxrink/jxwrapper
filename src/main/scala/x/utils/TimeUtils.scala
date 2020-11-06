package x.utils

import java.text.ParseException
import java.util.{Calendar, Date}

import org.apache.commons.lang.time.{DateFormatUtils, DateUtils}
import x.log.Xlog

/**
  * Created by xw on 2019/8/30.
  */
object TimeUtils {
  private val LOG = Xlog.getLogger(TimeUtils.getClass)

  val SEC_MS = 1000

  val MIN_MS: Long = 60 * SEC_MS

  val HOUR_MS: Long = 60 * MIN_MS

  val DAY_MS: Long = 24 * HOUR_MS

  /**
    * 时间格式化
    *
    * @param date    时间
    * @param pattern 格式
    * @return
    */
  def format(date: Date, pattern: String): String = {
    DateFormatUtils.format(date, pattern)
  }

  /**
    * 格式化成yyyyMMddHHmmss
    *
    * @param date 格式化对象
    * @return
    */
  def format1(date: Date): String = {
    format(date, "yyyyMMddHHmmss")
  }

  /**
    * 格式化成yyyy-MM-dd HH:mm:ss
    *
    * @param date 格式化对象
    * @return
    */
  def format2(date: Date): String = {
    format(date, "yyyy-MM-dd HH:mm:ss")
  }

  /**
    * 字符串转Date类型
    *
    * @param time   时间
    * @param format 格式
    * @return
    */
  def parese(time: String, format: String): Date = {
    try {
      return DateUtils.parseDate(time, Array[String](format))
    } catch {
      case e: ParseException => LOG.error("", e)
    }
    null
  }

  /**
    * 字符串yyyyMMddHHmmss转Date类型
    *
    * @param time 时间串
    * @return
    */
  def parese1(time: String): Date = {
    parese(time, "yyyyMMddHHmmss")
  }

  /**
    * 字符串yyyy-MM-dd HH:mm:ss转Date类型
    *
    * @param time 时间串
    * @return
    */
  def parese2(time: String): Date = {
    parese(time, "yyyy-MM-dd HH:mm:ss")
  }

  /**
    * 获取当前时间
    *
    * @return
    */
  def getNow: Date = {
    Calendar.getInstance.getTime
  }

  /**
    * 获取时间戳 yyyyMMddHHmmss
    *
    * @return
    */
  def getTimestamp: String = {
    format1(getNow)
  }

  /**
    * 获取指定时间n秒前的时间
    *
    * @param date   日期
    * @param second 多少秒
    * @return
    */
  def getNowBeforeBySecond(date: Date, second: Int): Date = {
    DateUtils.addSeconds(date, -second)
  }

  /**
    * 获取当前n分钟前的时间
    *
    * @param min 多少分钟
    * @return
    */
  def getNowBeforeByMinutes(min: Int): Date = {
    DateUtils.addMinutes(getNow, -min)
  }

  /**
    * 获取当前n分钟前的时间不包含秒
    *
    * @param min 多少分钟
    * @return
    */
  def getNowBeforeByMinuteAbs(min: Int): Date = {
    val cal = Calendar.getInstance()
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(Calendar.SECOND, 0)
    cal.add(Calendar.MINUTE, -min)
    cal.getTime
  }

  /**
    * 获取当前n小时前的时间
    *
    * @param hour 多少小时
    * @return
    */
  def getNowBeforeByHour(hour: Int): Date = {
    val cal = Calendar.getInstance()
    cal.add(Calendar.HOUR, -hour)
    cal.getTime
  }

  /**
    * 获取当前n小时前的时间不包含分钟
    *
    * @param hour 多少小时
    * @return
    */
  def getNowBeforeByHourAbs(hour: Int): Date = {
    val cal = Calendar.getInstance()
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.add(Calendar.HOUR, -hour)
    cal.getTime
  }

  /**
    * 获取指定date的n天前的时间(开始时间)
    *
    * @param day 多少天
    * @return
    */
  def getNowBeforeByDay(date: Date, day: Int): Date = {
    val cal = Calendar.getInstance()
    cal.setTime(date)
    cal.add(Calendar.DATE, -day)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.getTime
  }

  /**
    * 获取当前n天前的时间(开始时间0:0:0)
    *
    * @param day 多少天
    * @return
    */
  def getNowBeforeByDay(day: Int): Date = {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DATE, -day)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.getTime
  }

  /**
    * 获取当前n天前的时间（结束时间23:59:59）
    *
    * @param day 多少天
    * @return
    */
  def getNowBeforeByDay2(day: Int): Date = {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DATE, -day)
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 0)
    cal.getTime
  }

  /**
    * 获取当前n月前的时间
    *
    * @param month 多少月
    * @return
    */
  def getNowBeforeByMonth(month: Int): Date = {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.add(Calendar.MONTH, 0 - month)
    cal.getTime
  }

  /**
    * 获取一段时间的天数
    *
    * @param start  开始日期
    * @param end    结束日期
    * @param offset 添加n天
    * @return
    */
  def getDays(start: Date, end: Date, offset: Int): Int = {
    if (null == start || null == end) {
      1
    } else {
      val s = DateUtils.getFragmentInDays(start, Calendar.YEAR)
      val e = DateUtils.getFragmentInDays(end, Calendar.YEAR)
      (e - s + offset).asInstanceOf[Int]
    }
  }

  /**
    * 获取一段时间的天数
    *
    * @param start 开始日期
    * @param end   结束日期
    * @return
    */
  def getDays(start: Date, end: Date): Int = {
    getDays(start, end, 0)
  }

  /**
    * 获取一段时间的月数
    *
    * @param start 开始日期
    * @param end   结束日期
    * @return
    */
  def getMonths(start: Date, end: Date): Int = {
    var months = 0

    val st = Calendar.getInstance()
    st.setTime(start)

    val ed = Calendar.getInstance()
    ed.setTime(end)

    while (st.before(ed)) {
      st.add(Calendar.MONTH, 1)
      months = months + 1
    }

    months
  }

  /**
    * 获取一段时间毫秒数
    *
    * @param start 开始日期
    * @param end   结束日期
    * @return
    */
  def getMillisecond(start: Date, end: Date): Long = {
    end.getTime - start.getTime
  }

  /**
    * 获取本周第一天的日期
    *
    * @return
    */
  def getFirstDayOfWeek: Date = {
    val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    getNowBeforeByDay(dayOfWeek - 2)
  }

  /**
    * 获取n周前的星期一
    *
    * @return
    */
  def getFirstDayOfBeforeWeek(n: Int): Date = {
    val date = getFirstDayOfWeek
    getNowBeforeByDay(date, n * 7)
  }

  /**
    * 获取本月第一天的日期
    *
    * @return
    */
  def getFirstDayOfMonth: Date = {
    val dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    getNowBeforeByDay(dayOfMonth - 1)
  }

  /**
    * 获取指定月份的第一天的日期
    *
    * @return
    */
  def getFirstDayOfMonth(date: Date): Date = {
    val cal = Calendar.getInstance()
    cal.setTime(date)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.getTime
  }

  /**
    * 获取上一个月第一天的日期,如:2017-05-01 00:00:00
    *
    * @return
    */
  def getFirstDayOfBeforeMonth: Date = {
    getFirstDayOfMonth(getNowBeforeByMonth(1))
  }

  /**
    * 获取本月最后一天
    *
    * @return
    */
  def getLastDayOfMonth: Date = {
    getLastDayOfMonth(getNowBeforeByMonth(0))
  }

  /**
    * 获取指定月最后一天
    *
    * @return
    */
  def getLastDayOfMonth(nextMonth: Date): Date = {
    val cal = Calendar.getInstance()
    cal.setTime(nextMonth)
    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 999)
    cal.add(Calendar.DATE, -1)
    cal.getTime
  }

  /**
    * 获取上月最后一天,如:2017-05-31 23:59:59
    *
    * @return
    */
  def getLastDayOfBeforeMonth: Date = {
    getLastDayOfMonth(getNowBeforeByMonth(1))
  }
}
