package dev.yong.wheel.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author CoderYong
 */
public class TimeUtils {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final int MILLISECOND = 1;
    public static final int SECOND = 1000;
    public static final int MINUTE = 60000;
    public static final int HOUR = 3600000;
    public static final int DAY = 86400000;

    @IntDef({MILLISECOND, SECOND, MINUTE, HOUR, DAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Unit {
    }

    private TimeUtils() {
        throw new UnsupportedOperationException("Cannot be created");
    }

    /**
     * 获默认格式日期当前日期（默认格式为 年-月-日 时：分：秒）
     * <p>默认格式为 yyyy-MM-dd HH:mm:ss</p>
     *
     * @return 字符串日期
     */
    public static String getCurrentDate() {
        return getDate(System.currentTimeMillis(), DEFAULT_FORMAT);
    }

    /**
     * 获指定格式当前日期（默认格式为 年-月-日 时：分：秒）
     * <p>默认格式为 yyyy-MM-dd HH:mm:ss</p>
     *
     * @return 字符串日期
     */
    public static String getCurrentDate(String strFormat) {
        return getDate(System.currentTimeMillis(), strFormat);
    }

    /**
     * 获默认格式日期（默认格式为 年-月-日 时：分：秒）
     * <p>默认格式为 yyyy-MM-dd HH:mm:ss</p>
     *
     * @param millis 毫秒时间戳
     * @return 字符串日期
     */
    public static String getDate(long millis) {
        return getDate(millis + "", DEFAULT_FORMAT);
    }

    /**
     * 获默认格式日期（默认格式为 年-月-日 时：分：秒）
     * <p>默认格式为 yyyy-MM-dd HH:mm:ss</p>
     *
     * @param millisStr 字符串毫秒时间戳
     * @return 字符串日期
     */
    public static String getDate(String millisStr) {
        return getDate(millisStr, DEFAULT_FORMAT);
    }

    /**
     * 获取格式日期（默认格式为 年-月-日 时：分：秒）
     * <p>默认格式为 yyyy-MM-dd HH:mm:ss</p>
     *
     * @param millis    毫秒时间戳
     * @param strFormat 日期格式
     * @return 字符串日期
     */
    public static String getDate(long millis, String strFormat) {
        return getDate(millis + "", strFormat);
    }

    /**
     * 获取格式日期（默认格式为 年-月-日 时：分：秒）
     * <p>默认格式为 yyyy-MM-dd HH:mm:ss</p>
     *
     * @param millisStr 字符串毫秒时间戳
     * @param strFormat 日期格式
     * @return 字符串日期
     */
    public static String getDate(String millisStr, String strFormat) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(strFormat, Locale.getDefault());
            return format.format(new Date(Long.parseLong(millisStr)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Invalid timestamp");
        }
    }

    /**
     * 获取默认时间戳（默认格式为 年-月-日 时：分：秒）
     * <p>默认格式为 yyyy-MM-dd HH:mm:ss</p>
     *
     * @param date 日期
     * @return 时间戳
     */
    public static long getMillis(String date) {
        return getMillis(date, DEFAULT_FORMAT);
    }

    /**
     * 获取时间戳（默认格式为 年-月-日 时：分：秒）
     * <p>默认格式为 yyyy-MM-dd HH:mm:ss</p>
     *
     * @param date      日期
     * @param strFormat 日期格式
     * @return 时间戳
     */
    public static long getMillis(String date, String strFormat) {
        SimpleDateFormat format = new SimpleDateFormat(strFormat, Locale.getDefault());
        try {
            return format.parse(date).getTime();
        } catch (ParseException e) {
            throw new IllegalStateException("Invalid date");
        }
    }

    /**
     * 获取指定日期与当前时间的间隔时间
     *
     * @param date 日期
     * @param unit 跨度单位
     *             <ul>
     *             <li>{@link #MILLISECOND}</li>
     *             <li>{@link #SECOND }</li>
     *             <li>{@link #MINUTE }</li>
     *             <li>{@link #HOUR}</li>
     *             <li>{@link #DAY }</li>
     *             </ul>
     * @return 返回间隔时间
     */
    public static long getTimeSpanByNow(String date, @Unit int unit) {
        return getTimeSpan(System.currentTimeMillis(), getMillis(date), unit);
    }

    /**
     * 获取指定日期与当前时间的间隔时间
     *
     * @param date      日期
     * @param strFormat 日期格式
     * @param unit      跨度单位
     *                  <ul>
     *                  <li>{@link #MILLISECOND}</li>
     *                  <li>{@link #SECOND }</li>
     *                  <li>{@link #MINUTE }</li>
     *                  <li>{@link #HOUR}</li>
     *                  <li>{@link #DAY }</li>
     *                  </ul>
     * @return 返回间隔时间
     */
    public static long getTimeSpanByNow(String date, String strFormat, @Unit int unit) {
        return getTimeSpan(getMillis(getCurrentDate(strFormat)), getMillis(date, strFormat), unit);
    }

    /**
     * 获取指定时间与当前时间的间隔时间
     *
     * @param millis 毫秒时间戳
     * @param unit   跨度单位
     *               <ul>
     *               <li>{@link #MILLISECOND}</li>
     *               <li>{@link #SECOND }</li>
     *               <li>{@link #MINUTE }</li>
     *               <li>{@link #HOUR}</li>
     *               <li>{@link #DAY }</li>
     *               </ul>
     * @return 返回间隔时间
     */
    public static long getTimeSpanByNow(long millis, @Unit int unit) {
        return getTimeSpan(System.currentTimeMillis(), millis, unit);
    }

    /**
     * 获取间隔时间
     *
     * @param date1 日期1
     * @param date2 日期2
     * @param unit  跨度单位
     *              <ul>
     *              <li>{@link #MILLISECOND}</li>
     *              <li>{@link #SECOND }</li>
     *              <li>{@link #MINUTE }</li>
     *              <li>{@link #HOUR}</li>
     *              <li>{@link #DAY }</li>
     *              </ul>
     * @return 返回间隔时间
     */
    public static long getTimeSpan(String date1, String date2, @Unit int unit) {
        return millis2TimeSpan(Math.abs(getMillis(date1) - getMillis(date2)), unit);
    }

    /**
     * 获取间隔时间
     *
     * @param date1     日期1
     * @param date2     日期2
     * @param strFormat 日期格式
     * @param unit      跨度单位
     *                  <ul>
     *                  <li>{@link #MILLISECOND}</li>
     *                  <li>{@link #SECOND }</li>
     *                  <li>{@link #MINUTE }</li>
     *                  <li>{@link #HOUR}</li>
     *                  <li>{@link #DAY }</li>
     *                  </ul>
     * @return 返回间隔时间
     */
    public static long getTimeSpan(String date1, String date2, String strFormat, @Unit int unit) {
        return millis2TimeSpan(Math.abs(
                getMillis(date1, strFormat) - getMillis(date2, strFormat)), unit);
    }

    /**
     * 获取间隔时间
     *
     * @param millis1 毫秒时间戳1
     * @param millis2 毫秒时间戳2
     * @param unit    跨度单位
     *                <ul>
     *                <li>{@link #MILLISECOND}</li>
     *                <li>{@link #SECOND}</li>
     *                <li>{@link #MINUTE}</li>
     *                <li>{@link #HOUR}</li>
     *                <li>{@link #DAY}</li>
     *                </ul>
     * @return 返回间隔时间
     */
    public static long getTimeSpan(long millis1, long millis2, @Unit int unit) {
        return millis2TimeSpan(Math.abs(millis1 - millis2), unit);
    }

    private static long timeSpan2Millis(long timeSpan, @Unit int unit) {
        return timeSpan * unit;
    }

    private static long millis2TimeSpan(long millis, @Unit int unit) {
        return millis / unit;
    }

    /**
     * 获取指定日期与当前时间带有精确单位的时间间隔
     *
     * @param date 日期
     * @param unit 跨度单位
     *             <ul>
     *             <li>{@link #DAY} return 天</li>
     *             <li>{@link #HOUR} return 天, 小时</li>
     *             <li>{@link #MINUTE} return 天, 小时, 分钟</li>
     *             <li>{@link #SECOND}return 天, 小时, 分钟, 秒</li>
     *             <li>{@link #MILLISECOND} return 天, 小时, 分钟, 秒, 毫秒</li>
     *             </ul>
     * @return 返回间隔时间
     */
    public static String getFitTimeSpanByNow(String date, @Unit int unit) {
        return getFitTimeSpan(System.currentTimeMillis(), getMillis(date), unit);
    }

    /**
     * 获取指定日期与当前时间带有精确单位的时间间隔
     *
     * @param date 日期
     * @param unit 跨度单位
     *             <ul>
     *             <li>{@link #DAY} return 天</li>
     *             <li>{@link #HOUR} return 天, 小时</li>
     *             <li>{@link #MINUTE} return 天, 小时, 分钟</li>
     *             <li>{@link #SECOND}return 天, 小时, 分钟, 秒</li>
     *             <li>{@link #MILLISECOND} return 天, 小时, 分钟, 秒, 毫秒</li>
     *             </ul>
     * @return 返回间隔时间
     */
    public static String getFitTimeSpanByNow(String date, String strFormat, @Unit int unit) {
        return getFitTimeSpan(getMillis(getCurrentDate(strFormat), strFormat), getMillis(date, strFormat), unit);
    }

    /**
     * 获取指定时间与当前时间带有精确单位的时间间隔
     *
     * @param millis 毫秒时间戳
     * @param unit   跨度单位
     *               <ul>
     *               <li>{@link #DAY} return 天</li>
     *               <li>{@link #HOUR} return 天, 小时</li>
     *               <li>{@link #MINUTE} return 天, 小时, 分钟</li>
     *               <li>{@link #SECOND}return 天, 小时, 分钟, 秒</li>
     *               <li>{@link #MILLISECOND} return 天, 小时, 分钟, 秒, 毫秒</li>
     *               </ul>
     * @return 返回间隔时间
     */
    public static String getFitTimeSpanByNow(long millis, @Unit int unit) {
        return getFitTimeSpan(System.currentTimeMillis(), millis, unit);
    }

    /**
     * 获取带有精确单位的时间间隔
     *
     * @param date1 日期1
     * @param date2 日期2
     * @param unit  跨度单位
     *              <ul>
     *              <li>{@link #DAY} return 天</li>
     *              <li>{@link #HOUR} return 天, 小时</li>
     *              <li>{@link #MINUTE} return 天, 小时, 分钟</li>
     *              <li>{@link #SECOND}return 天, 小时, 分钟, 秒</li>
     *              <li>{@link #MILLISECOND} return 天, 小时, 分钟, 秒, 毫秒</li>
     *              </ul>
     * @return 返回间隔时间
     */
    public static String getFitTimeSpan(String date1, String date2, @Unit int unit) {
        return millis2FitTimeSpan(Math.abs(getMillis(date1) - getMillis(date2)), unit);
    }

    /**
     * 获取带有精确单位的时间间隔
     *
     * @param date1     日期1
     * @param date2     日期2
     * @param strFormat 日期格式
     * @param unit      跨度单位
     *                  <ul>
     *                  <li>{@link #DAY} return 天</li>
     *                  <li>{@link #HOUR} return 天, 小时</li>
     *                  <li>{@link #MINUTE} return 天, 小时, 分钟</li>
     *                  <li>{@link #SECOND}return 天, 小时, 分钟, 秒</li>
     *                  <li>{@link #MILLISECOND} return 天, 小时, 分钟, 秒, 毫秒</li>
     *                  </ul>
     * @return 返回间隔时间
     */
    public static String getFitTimeSpan(String date1, String date2, String strFormat, @Unit int unit) {
        return millis2FitTimeSpan(Math.abs(getMillis(date1, strFormat) - getMillis(date2, strFormat)), unit);
    }

    /**
     * 获取带有精确单位的时间间隔
     *
     * @param millis1 毫秒时间戳1
     * @param millis2 毫秒时间戳2
     * @param unit    跨度单位
     *                <ul>
     *                <li>{@link #DAY} return 天</li>
     *                <li>{@link #HOUR} return 天, 小时</li>
     *                <li>{@link #MINUTE} return 天, 小时, 分钟</li>
     *                <li>{@link #SECOND}return 天, 小时, 分钟, 秒</li>
     *                <li>{@link #MILLISECOND} return 天, 小时, 分钟, 秒, 毫秒</li>
     *                </ul>
     * @return 返回间隔时间
     */
    public static String getFitTimeSpan(long millis1, long millis2, @Unit int unit) {
        return millis2FitTimeSpan(Math.abs(millis1 - millis2), unit);
    }

    private static int[] UNITS = {DAY, HOUR, MINUTE, SECOND, MILLISECOND};
    private static String millis2FitTimeSpan(long millis, @Unit int unit) {
        int precision = 4;
        for (int i = 0; i < UNITS.length; i++) {
            if (unit == UNITS[i]) {
                precision = i;
                break;
            }
        }
        String[] units = {"天", "小时", "分钟", "秒", "毫秒"};
        if (millis == 0) {
            return 0 + units[precision];
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < precision + 1; i++) {
            if (millis >= UNITS[i]) {
                long mode = millis / UNITS[i];
                millis -= mode * UNITS[i];
                sb.append(mode).append(units[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 获取指定日期与当前时间间隔状态
     *
     * @param date 日期
     * @return 时间间隔状态
     * <ul>
     * <li>如果小于 1 秒钟内，显示刚刚</li>
     * <li>如果在 1 分钟内，显示 XXX秒前</li>
     * <li>如果在 1 小时内，显示 XXX分钟前</li>
     * <li>如果在 1 小时外的今天内，显示今天15:32</li>
     * <li>如果是昨天的，显示昨天15:32</li>
     * <li>其余显示，2016-10-15</li>
     * <li>时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007</li>
     * </ul>
     */
    public static String getTimeSpanStatusByNow(String date) {
        return getTimeSpanStatusByNow(getMillis(date));
    }

    /**
     * 获取指定日期与当前时间间隔状态
     *
     * @param date      日期
     * @param strFormat 日期格式
     * @return 时间间隔状态
     * <ul>
     * <li>如果小于 1 秒钟内，显示刚刚</li>
     * <li>如果在 1 分钟内，显示 XXX秒前</li>
     * <li>如果在 1 小时内，显示 XXX分钟前</li>
     * <li>如果在 1 小时外的今天内，显示今天15:32</li>
     * <li>如果是昨天的，显示昨天15:32</li>
     * <li>其余显示，2016-10-15</li>
     * <li>时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007</li>
     * </ul>
     */
    public static String getTimeSpanStatusByNow(String date, String strFormat) {
        return getTimeSpanStatusByNow(getMillis(date, strFormat));
    }

    /**
     * 获取指定时间与当前时间间隔状态
     *
     * @param millis 毫秒时间戳
     * @return 时间间隔状态
     * <ul>
     * <li>如果小于 1 秒钟内，显示刚刚</li>
     * <li>如果在 1 分钟内，显示 XXX秒前</li>
     * <li>如果在 1 小时内，显示 XXX分钟前</li>
     * <li>如果在 1 小时外的今天内，显示今天15:32</li>
     * <li>如果是昨天的，显示昨天15:32</li>
     * <li>其余显示，2016-10-15</li>
     * <li>时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007</li>
     * </ul>
     */
    public static String getTimeSpanStatusByNow(long millis) {
        long now = System.currentTimeMillis();
        long span = now - millis;
        if (span < 0) {
            return String.format("%tc", millis);
        }
        if (span < 1000) {
            return "刚刚";
        } else if (span < MINUTE) {
            return String.format(Locale.getDefault(), "%d秒前", span / SECOND);
        } else if (span < HOUR) {
            return String.format(Locale.getDefault(), "%d分钟前", span / MINUTE);
        }
        // 获取当天 00:00
        long wee = getTodayZero();
        if (millis >= wee) {
            return String.format("今天%tR", millis);
        } else if (millis >= wee - DAY) {
            return String.format("昨天%tR", millis);
        } else {
            return String.format("%tF", millis);
        }
    }

    /**
     * 获取指定日期在指定时间间隔的时间戳
     *
     * @param date     指定日期
     * @param timeSpan 指定时间间隔
     * @param unit     跨度单位
     *                 <ul>
     *                 <li>{@link #MILLISECOND}</li>
     *                 <li>{@link #SECOND }</li>
     *                 <li>{@link #MINUTE }</li>
     *                 <li>{@link #HOUR}</li>
     *                 <li>{@link #DAY }</li>
     *                 </ul>
     * @return 返回间隔获取的时间戳
     */
    public static long getMillis(String date, long timeSpan, @Unit final int unit) {
        return getMillis(date) + timeSpan2Millis(timeSpan, unit);
    }

    /**
     * 获取指定日期在指定时间间隔的时间戳
     *
     * @param date      指定日期
     * @param strFormat 日期格式
     * @param timeSpan  指定时间间隔
     * @param unit      跨度单位
     *                  <ul>
     *                  <li>{@link #MILLISECOND}</li>
     *                  <li>{@link #SECOND }</li>
     *                  <li>{@link #MINUTE }</li>
     *                  <li>{@link #HOUR}</li>
     *                  <li>{@link #DAY }</li>
     *                  </ul>
     * @return 返回间隔获取的时间戳
     */
    public static long getMillis(String date, String strFormat, long timeSpan, @Unit int unit) {
        return getMillis(date, strFormat) + timeSpan2Millis(timeSpan, unit);
    }

    /**
     * 获取指定时间在指定时间间隔的时间戳
     *
     * @param millis   指定时间戳
     * @param timeSpan 指定时间间隔
     * @param unit     跨度单位
     *                 <ul>
     *                 <li>{@link #MILLISECOND}</li>
     *                 <li>{@link #SECOND }</li>
     *                 <li>{@link #MINUTE }</li>
     *                 <li>{@link #HOUR}</li>
     *                 <li>{@link #DAY }</li>
     *                 </ul>
     * @return 返回间隔获取的时间戳
     */
    public static long getMillis(long millis, long timeSpan, @Unit int unit) {
        return millis + timeSpan2Millis(timeSpan, unit);
    }

    /**
     * 获取指定时间在指定时间间隔的日期
     *
     * @param millis   指定时间戳
     * @param timeSpan 指定时间间隔
     * @param unit     跨度单位
     *                 <ul>
     *                 <li>{@link #MILLISECOND}</li>
     *                 <li>{@link #SECOND }</li>
     *                 <li>{@link #MINUTE }</li>
     *                 <li>{@link #HOUR}</li>
     *                 <li>{@link #DAY }</li>
     *                 </ul>
     * @return 返回间隔获取的时间戳
     */
    public static String getDate(long millis, long timeSpan, @Unit int unit) {
        return getDate(getMillis(millis, timeSpan, unit), DEFAULT_FORMAT);
    }

    /**
     * 获取指定时间在指定时间间隔的日期
     *
     * @param millis    指定时间戳
     * @param strFormat 日期格式
     * @param timeSpan  指定时间间隔
     * @param unit      跨度单位
     *                  <ul>
     *                  <li>{@link #MILLISECOND}</li>
     *                  <li>{@link #SECOND }</li>
     *                  <li>{@link #MINUTE }</li>
     *                  <li>{@link #HOUR}</li>
     *                  <li>{@link #DAY }</li>
     *                  </ul>
     * @return 返回间隔获取的时间戳
     */
    public static String getDate(long millis, String strFormat, long timeSpan, @Unit int unit) {
        return getDate(getMillis(millis, timeSpan, unit), strFormat);
    }

    /**
     * 获取指定日期在指定时间间隔的日期
     *
     * @param date     指定时间戳
     * @param timeSpan 指定时间间隔
     * @param unit     跨度单位
     *                 <ul>
     *                 <li>{@link #MILLISECOND}</li>
     *                 <li>{@link #SECOND }</li>
     *                 <li>{@link #MINUTE }</li>
     *                 <li>{@link #HOUR}</li>
     *                 <li>{@link #DAY }</li>
     *                 </ul>
     * @return 返回间隔获取的时间戳
     */
    public static String getDate(String date, long timeSpan, @Unit int unit) {
        return getDate(getMillis(date, timeSpan, unit));
    }

    /**
     * 获取指定日期在指定时间间隔的日期
     *
     * @param date      指定时间戳
     * @param strFormat 日期格式
     * @param timeSpan  指定时间间隔
     * @param unit      跨度单位
     *                  <ul>
     *                  <li>{@link #MILLISECOND}</li>
     *                  <li>{@link #SECOND }</li>
     *                  <li>{@link #MINUTE }</li>
     *                  <li>{@link #HOUR}</li>
     *                  <li>{@link #DAY }</li>
     *                  </ul>
     * @return 返回间隔获取的时间戳
     */
    public static String getDate(String date, String strFormat, long timeSpan, @Unit int unit) {
        return getDate(getMillis(date, strFormat, timeSpan, unit));
    }

    /**
     * 获取当前时间在指定时间间隔的时间戳
     *
     * @param timeSpan 指定时间间隔
     * @param unit     跨度单位
     *                 <ul>
     *                 <li>{@link #MILLISECOND}</li>
     *                 <li>{@link #SECOND }</li>
     *                 <li>{@link #MINUTE }</li>
     *                 <li>{@link #HOUR}</li>
     *                 <li>{@link #DAY }</li>
     *                 </ul>
     * @return 返回间隔获取的时间戳
     */
    public static long getMillisByNow(long timeSpan, @Unit int unit) {
        return getMillis(System.currentTimeMillis(), timeSpan, unit);
    }

    /**
     * 获取当前时间在指定时间间隔的日期
     *
     * @param timeSpan 指定时间间隔
     * @param unit     跨度单位
     *                 <ul>
     *                 <li>{@link #MILLISECOND}</li>
     *                 <li>{@link #SECOND }</li>
     *                 <li>{@link #MINUTE }</li>
     *                 <li>{@link #HOUR}</li>
     *                 <li>{@link #DAY }</li>
     *                 </ul>
     * @return 返回间隔获取的时间戳
     */
    public static String getDateByNow(long timeSpan, @Unit int unit) {
        return getDate(getMillisByNow(timeSpan, unit));
    }

    /**
     * 获取当前时间在指定时间间隔的日期
     *
     * @param strFormat 日期格式
     * @param timeSpan  指定时间间隔
     * @param unit      跨度单位
     *                  <ul>
     *                  <li>{@link #MILLISECOND}</li>
     *                  <li>{@link #SECOND }</li>
     *                  <li>{@link #MINUTE }</li>
     *                  <li>{@link #HOUR}</li>
     *                  <li>{@link #DAY }</li>
     *                  </ul>
     * @return 返回间隔获取的时间戳
     */
    public static String getDateByNow(String strFormat, long timeSpan, @Unit int unit) {
        return getDate(getMillisByNow(timeSpan, unit), strFormat);
    }

    /**
     * 判断指定日期是否是今天
     *
     * @param date 毫秒时间戳
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(String date) {
        return isToday(getMillis(date));
    }

    /**
     * 判断指定日期是否是今天
     *
     * @param date 毫秒时间戳
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(String date, String strFormat) {
        return isToday(getMillis(date, strFormat));
    }

    /**
     * Return whether it is today.
     *
     * @param date The date.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(final Date date) {
        return isToday(date.getTime());
    }

    /**
     * 判断指定时间是否是今天
     *
     * @param millis 毫秒时间戳
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isToday(long millis) {
        long wee = getTodayZero();
        return millis >= wee && millis < wee + DAY;
    }

    /**
     * 是否是闰年
     * <p>默认日期格式 yyyy-MM-dd HH:mm:ss</p>
     *
     * @param date 日期
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLeapYear(String date) {
        return isLeapYear(getMillis(date, DEFAULT_FORMAT));
    }

    /**
     * 是否是闰年
     *
     * @param date      日期
     * @param strFormat 日期格式
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLeapYear(String date, String strFormat) {
        return isLeapYear(getMillis(date, strFormat));
    }

    /**
     * 是否是闰年
     *
     * @param millis 毫秒时间戳
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLeapYear(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        int year = cal.get(Calendar.YEAR);
        return isLeapYear(year);
    }

    /**
     * 是否是闰年
     *
     * @param year 年份
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    /**
     * 获取指定日期为周几
     *
     * @param date 日期
     * @return 周几
     */
    public static String getWeek(String date) {
        return getWeek(getMillis(date));
    }

    /**
     * 获取指定日期为周几
     *
     * @param date      日期
     * @param strFormat 日期格式
     * @return 周几
     */
    public static String getWeek(String date, String strFormat) {
        return getWeek(getMillis(date, strFormat));
    }

    /**
     * 获取指定时间为周几
     *
     * @param millis 毫秒时间戳
     * @return 周几
     */
    public static String getWeek(long millis) {
        return new SimpleDateFormat("E", Locale.getDefault()).format(new Date(millis));
    }

    public static long getTodayZero() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static int getValueByCalendarField(String date, int field) {
        return getValueByCalendarField(getMillis(date), field);
    }

    public static int getValueByCalendarField(String time, String strFormat, int field) {
        return getValueByCalendarField(getMillis(time, strFormat), field);
    }

    public static int getValueByCalendarField(long millis, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal.get(field);
    }

    /**
     * 获取当天某时的毫秒值
     *
     * @param hour 小时
     * @return 当天某时对应时间戳
     */
    public static long getTodayHour(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当月天数
     *
     * @return 当月天数
     */
    public static int getCurrentMonthDayCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //获取指定年份中指定月份有几天
        return calendar.getActualMaximum(Calendar.DATE);
    }

    /**
     * 获取当月第一天的时间戳
     * <p>0时0分0秒</p>
     *
     * @return 当月第一天的时间戳
     */
    public static long getCurrentMonthFirstDayTime() {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当月最后一天的时间戳
     * <p>23时59分59秒</p>
     *
     * @return 当月最后一天的时间戳
     */
    public static long getCurrentMonthLastDayTime() {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTimeInMillis();
    }

    /**************************************** 十二星座 ****************************************/

    private static final int[] CONSTELLATIONS_FLAGS = {20, 19, 21, 21, 21, 22, 23, 23, 23, 24, 23, 22};
    private static final String[] CONSTELLATIONS = {
            "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
            "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座"
    };

    /**
     * 获取指定日期的星座
     *
     * @param date 日期
     * @return 星座 {@link #CONSTELLATIONS}
     */
    public static String getConstellations(String date) {
        return getConstellations(getMillis(date));
    }

    /**
     * 获取指定日期的星座
     *
     * @param date      日期
     * @param strFormat 日期格式
     * @return 星座 {@link #CONSTELLATIONS}
     */
    public static String getConstellations(String date, String strFormat) {
        return getConstellations(getMillis(date, strFormat));
    }

    /**
     * 获取星座
     *
     * @param millis 毫秒时间戳
     * @return 星座 {@link #CONSTELLATIONS}
     */
    public static String getConstellations(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return getConstellations(month, day);
    }

    /**
     * 获取星座
     *
     * @param month 月份
     * @param day   日
     * @return 星座 {@link #CONSTELLATIONS}
     */
    public static String getConstellations(int month, int day) {
        return CONSTELLATIONS[day >= CONSTELLATIONS_FLAGS[month - 1]
                ? month - 1
                : (month + 10) % 12];
    }
}
