package x.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;

public class TimeUtils {
    private static final Log LOG = JxUtils.getLogger(TimeUtils.class);

    public static final long SEC_MS = 1000;

    public static final long MIN_MS = 60 * SEC_MS;

    public static final long HOUR_MS = 60 * MIN_MS;

    public static final long DAY_MS = 24 * HOUR_MS;

    /**
     * 时间格式化
     *
     * @param date    时间
     * @param pattern 格式
     * @return
     */
    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 格式化成yyyyMMddHHmmSS
     *
     * @param date
     * @return
     */
    public static String format1(Date date) {
        return format(date, "yyyyMMddHHmmSS");
    }

    /**
     * 格式化成yyyy-MM-dd HH:mm:SS
     *
     * @param date
     * @return
     */
    public static String format2(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:SS");
    }

    /**
     * 字符串转Date类型 yyyyMMddHHmmss
     *
     * @param time
     * @return
     */
    public static Date parese1(String time) {
        return parese(time, "yyyyMMddHHmmSS");
    }

    /**
     * 字符串转Date类型 yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static Date parese2(String time) {
        return parese(time, "yyyy-MM-dd HH:mm:SS");
    }

    /**
     * 字符串转Date类型
     *
     * @param time   时间
     * @param format 格式
     * @return
     */
    public static Date parese(String time, String format) {
        try {
            return new SimpleDateFormat(format).parse(time);
        } catch (ParseException e) {
            LOG.error("", e);
        }
        return null;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date getNow() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 获取时间戳 yyyyMMddHHmmSS
     *
     * @return
     */
    public static String getTimestamp() {
        return format1(getNow());
    }

    /**
     * 获取指定时间n秒前的时间
     *
     * @param date
     * @param second
     * @return
     */
    public static Date getNowBeforeBySecond(Date date, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MILLISECOND, second);
        return cal.getTime();
    }

    /**
     * 获取当前n分钟前的时间
     *
     * @param min
     * @return
     */
    public static Date getNowBeforeByMinute(int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MINUTE, 0 - min);
        return cal.getTime();
    }

    /**
     * 获取当前n分钟前的时间不包含秒
     *
     * @param min
     * @return
     */
    public static Date getNowBeforeByMinuteAbs(int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.MINUTE, 0 - min);
        return cal.getTime();
    }

    /**
     * 获取当前n小时前的时间
     *
     * @param hour
     * @return
     */
    public static Date getNowBeforeByHour(int hour) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 0 - hour);
        return cal.getTime();
    }

    /**
     * 获取当前n小时前的时间不包含分钟
     *
     * @param hour
     * @return
     */
    public static Date getNowBeforeByHourAbs(int hour) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.add(Calendar.HOUR, 0 - hour);
        return cal.getTime();
    }

    /**
     * 获取指定date的n天前的时间(开始时间)
     *
     * @param day
     * @return
     */
    public static Date getNowBeforeByDate(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, (0 - day));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当前n天前的时间(开始时间0:0:0)
     *
     * @param day
     * @return
     */
    public static Date getNowBeforeByDay(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (0 - day));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当前n天前的时间（结束时间23:59:59）
     *
     * @param day
     * @return
     */
    public static Date getNowBeforeByDay2(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (0 - day));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当前n月前的时间
     *
     * @param month
     * @return
     */
    public static Date getNowBeforeByMonth(int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, 0 - month);
        return cal.getTime();
    }

    /**
     * 获取一段时间的天数,最少返回1天
     *
     * @param start
     * @param end
     * @param offset
     * @return
     */
    public static int getDays(Date start, Date end, int offset) {
        if (null == start || null == end) {
            return 1;
        }

        int day = new BigDecimal(end.getTime() - start.getTime()).divide(new BigDecimal(DAY_MS), 2).intValue();

        return 0 == day ? 1 : (day + offset);
    }

    /**
     * 获取一段时间的天数,最少返回1天
     *
     * @param start
     * @param end
     * @return
     */
    public static int getDays(Date start, Date end) {
        return getDays(start, end, 0);
    }

    /**
     * 获取一段时间的月数,最少返回1月
     *
     * @param start
     * @param end
     * @return
     */
    public static int getMonths(Date start, Date end) {
        int months = 1;

        Calendar st = Calendar.getInstance();
        st.setTime(start);

        Calendar ed = Calendar.getInstance();
        ed.setTime(end);

        while (st.before(ed)) {
            st.add(Calendar.MONTH, 1);
            months++;
        }

        return months;
    }

    /**
     * 获取一段时间毫秒数
     *
     * @param start
     * @param end
     * @return
     */
    public static long getMillisecond(Date start, Date end) {
        return end.getTime() - start.getTime();
    }

    /**
     * 获取本周第一天的日期
     *
     * @return
     */
    public static Date getFirstDayOfWeek() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return getNowBeforeByDay(dayOfWeek - 2);
    }

    /**
     * 获取n周前的星期一
     *
     * @return
     */
    public static Date getFirstDayOfBeforeWeek(int n) {
        Date date = getFirstDayOfWeek();
        return getNowBeforeByDate(date, n * 7);
    }

    /**
     * 获取本月第一天的日期
     *
     * @return
     */
    public static Date getFirstDayOfMonth() {
        int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return getNowBeforeByDay(dayOfMonth - 1);
    }

    /**
     * 获取指定月份的第一天的日期
     *
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取上一个月第一天的日期 2017-05-01 00:00:00
     *
     * @return
     */
    public static Date getFirstDayOfBeforeMonth() {
        return getFirstDayOfMonth(getNowBeforeByMonth(1));
    }

    /**
     * 获取本月最后一天
     *
     * @return
     */
    public static Date getLastDayOfMonth() {
        return getLastDayOfMonth(getNowBeforeByMonth(0));
    }

    /**
     * 获取指定月最后一天
     *
     * @return
     */
    public static Date getLastDayOfMonth(Date nextMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(nextMonth);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    /**
     * 获取上月最后一天 2017-05-31 23:59:59
     *
     * @return
     */
    public static Date getLastDayOfBeforeMonth() {
        return getLastDayOfMonth(getNowBeforeByMonth(1));
    }
}
