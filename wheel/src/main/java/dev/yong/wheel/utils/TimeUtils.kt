@file:Suppress("unused")

package dev.yong.wheel.utils

import androidx.annotation.IntDef
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@IntDef(MILLISECOND, SECOND, MINUTE, HOUR, DAY)
@Retention(AnnotationRetention.SOURCE)
annotation class Unit

const val DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss"
const val MILLISECOND = 1
const val SECOND = 1000
const val MINUTE = 60000
const val HOUR = 3600000
const val DAY = 86400000

/**
 * 获取当前日期（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param format 日期格式
 * @param unit   时间戳单位
 *
 *  * [MILLISECOND]
 *  * [SECOND]
 *  * [MINUTE]
 *  * [HOUR]
 *  * [DAY]
 *
 * @return 字符串日期
 */
fun currentDate(format: String = DEFAULT_FORMAT, @Unit unit: Int = MILLISECOND): String {
    return timestampToDate(System.currentTimeMillis(), format, unit)
}

/**
 * 毫秒转日期（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param millis 毫秒时间戳
 * @param format 日期格式
 *
 * @return 字符串日期
 */
fun millisToDate(millis: Long, format: String = DEFAULT_FORMAT): String {
    return timestampToDate(millis, format, MILLISECOND)
}

/**
 * 秒转日期（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param second 秒时间戳
 * @param format 日期格式
 *
 * @return 字符串日期
 */
fun secondToDate(second: Long, format: String = DEFAULT_FORMAT): String {
    return timestampToDate(second, format, SECOND)
}

/**
 * 分转日期（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param minute 分时间戳
 * @param format 日期格式
 *
 * @return 字符串日期
 */
fun minuteToDate(minute: Long, format: String = DEFAULT_FORMAT): String {
    return timestampToDate(minute, format, MINUTE)
}

/**
 * 时转日期（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param hour   小时时间戳
 * @param format 日期格式
 *
 * @return 字符串日期
 */
fun hourToDate(hour: Long, format: String = DEFAULT_FORMAT): String {
    return timestampToDate(hour, format, HOUR)
}

/**
 * 天转日期（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param day   天时间戳
 * @param format 日期格式
 *
 * @return 字符串日期
 */
fun dayToDate(day: Long, format: String = DEFAULT_FORMAT): String {
    return timestampToDate(day, format, DAY)
}

/**
 * 时间戳转日期（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param timestamp 字符串毫秒时间戳
 * @param format    日期格式
 * @param unit      时间戳单位
 *
 *  * [MILLISECOND]
 *  * [SECOND]
 *  * [MINUTE]
 *  * [HOUR]
 *  * [DAY]
 *
 * @return 字符串日期
 */
fun timestampToDate(timestamp: Long, format: String = DEFAULT_FORMAT, @Unit unit: Int): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(Date(timestamp * unit))
}

/**
 * 日期转毫秒（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param date   日期
 * @param format 日期格式
 *
 * @return 毫秒时间戳
 */
fun dateToMillis(date: String, format: String = DEFAULT_FORMAT): Long {
    return dateToTimestamp(date, format, MILLISECOND)
}

/**
 * 日期转秒（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param date   日期
 * @param format 日期格式
 *
 * @return 秒时间戳
 */
fun dateToSecond(date: String, format: String = DEFAULT_FORMAT): Long {
    return dateToTimestamp(date, format, SECOND)
}

/**
 * 日期转分（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param date   日期
 * @param format 日期格式
 *
 * @return 分时间戳
 */
fun dateToMinute(date: String, format: String = DEFAULT_FORMAT): Long {
    return dateToTimestamp(date, format, MINUTE)
}

/**
 * 日期转小时（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param date   日期
 * @param format 日期格式
 *
 * @return 小时时间戳
 */
fun dateToHour(date: String, format: String = DEFAULT_FORMAT): Long {
    return dateToTimestamp(date, format, HOUR)
}

/**
 * 日期转天（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param date   日期
 * @param format 日期格式
 *
 * @return 天时间戳
 */
fun dateToDay(date: String, format: String = DEFAULT_FORMAT): Long {
    return dateToTimestamp(date, format, DAY)
}

/**
 * 日期转时间戳（默认格式为 年-月-日 时:分:秒）
 *
 * 默认格式为 yyyy-MM-dd HH:mm:ss
 *
 * @param date   字符串日期
 * @param format 日期格式
 * @param unit   时间戳单位
 *
 *  * [MILLISECOND]
 *  * [SECOND]
 *  * [MINUTE]
 *  * [HOUR]
 *  * [DAY]
 *
 * @return 字符串日期
 */
fun dateToTimestamp(date: String, format: String = DEFAULT_FORMAT, @Unit unit: Int): Long {
    return SimpleDateFormat(format, Locale.getDefault()).parse(date)!!.time / unit
}

/**
 * 获取指定日期与当前时间的间隔时间
 *
 * @param date   日期
 * @param format 日期格式
 * @param unit   时间戳单位
 *
 *  * [MILLISECOND]
 *  * [SECOND]
 *  * [MINUTE]
 *  * [HOUR]
 *  * [DAY]
 *
 * @return 返回间隔时间
 */
fun dateSpanToNow(
    date: String,
    format: String = DEFAULT_FORMAT,
    @Unit unit: Int = MILLISECOND
): Long {
    return getTimeSpan(System.currentTimeMillis() / unit, dateToTimestamp(date, format, unit))
}

/**
 * 获取指定时间与当前时间的间隔时间
 *
 * @param time 时间戳
 * @param unit 时间戳单位
 *
 *  * [MILLISECOND]
 *  * [SECOND]
 *  * [MINUTE]
 *  * [HOUR]
 *  * [DAY]
 *
 * @return 返回间隔时间
 */
fun timeSpanToNow(time: Long, @Unit unit: Int): Long {
    return getTimeSpan(System.currentTimeMillis() / unit, time)
}

/**
 * 获取日期间隔时间
 *
 * @param beginDate 起始日期
 * @param endDate   结束日期
 * @param format    日期格式
 * @param unit      时间戳单位
 *
 *  * [MILLISECOND]
 *  * [SECOND]
 *  * [MINUTE]
 *  * [HOUR]
 *  * [DAY]
 *
 * @return 返回间隔时间
 */
fun getDateSpan(
    beginDate: String,
    endDate: String,
    format: String = DEFAULT_FORMAT,
    @Unit unit: Int = MILLISECOND
): Long {
    return abs(dateToTimestamp(beginDate, format, unit) - dateToTimestamp(endDate, format, unit))
}

/**
 * 获取时间间隔
 *
 * @param beginTime 起始时间戳
 * @param endTime   结束时间戳
 *
 * @return 返回间隔时间
 */
fun getTimeSpan(beginTime: Long, endTime: Long): Long {
    return abs(beginTime - endTime)
}

/**
 * 获取指定日期与当前时间带有精确单位的时间间隔
 *
 * @param date   日期
 * @param format 日期格式
 * @param unit   时间戳单位
 *
 *  * [DAY]         return 天
 *  * [HOUR]        return 天, 小时
 *  * [MINUTE]      return 天, 小时, 分钟
 *  * [SECOND]      return 天, 小时, 分钟, 秒
 *  * [MILLISECOND] return 天, 小时, 分钟, 秒, 毫秒
 *
 * @return 返回间隔时间
 */
fun fitDateSpanToNow(
    date: String,
    format: String = DEFAULT_FORMAT,
    @Unit unit: Int = MILLISECOND
): String {
    return getFitTimeSpan(System.currentTimeMillis(), dateToMillis(date, format), unit)
}

/**
 * 获取指定时间与当前时间带有精确单位的时间间隔
 *
 * @param millis 毫秒时间戳
 * @param unit   时间戳单位
 *
 *  * [DAY]         return 天
 *  * [HOUR]        return 天, 小时
 *  * [MINUTE]      return 天, 小时, 分钟
 *  * [SECOND]      return 天, 小时, 分钟, 秒
 *  * [MILLISECOND] return 天, 小时, 分钟, 秒, 毫秒
 *
 * @return 返回间隔时间
 */
fun fitTimeSpanToNow(millis: Long, @Unit unit: Int = MILLISECOND): String {
    return getFitTimeSpan(System.currentTimeMillis(), millis, unit)
}

/**
 * 获取带有精确单位的时间间隔
 *
 * @param beginDate 起始日期
 * @param endDate   结束日期
 * @param unit      时间戳单位
 *
 *  * [DAY]         return 天
 *  * [HOUR]        return 天, 小时
 *  * [MINUTE]      return 天, 小时, 分钟
 *  * [SECOND]      return 天, 小时, 分钟, 秒
 *  * [MILLISECOND] return 天, 小时, 分钟, 秒, 毫秒
 *
 * @return 返回间隔时间
 */
fun getFitDateSpan(
    beginDate: String,
    endDate: String,
    format: String = DEFAULT_FORMAT,
    @Unit unit: Int = MILLISECOND
): String {
    return getFitTimeSpan(
        dateToMillis(beginDate, format),
        dateToMillis(endDate, format),
        unit
    )
}


/**
 * 获取带有精确单位的时间间隔
 *
 * @param beginMillis 起始时间
 * @param endMillis   结束时间
 * @param unit        时间戳单位
 *
 *  * [DAY]         return 天
 *  * [HOUR]        return 天, 小时
 *  * [MINUTE]      return 天, 小时, 分钟
 *  * [SECOND]      return 天, 小时, 分钟, 秒
 *  * [MILLISECOND] return 天, 小时, 分钟, 秒, 毫秒
 *
 * @return 返回间隔时间
 */
fun getFitTimeSpan(beginMillis: Long, endMillis: Long, @Unit unit: Int = MILLISECOND): String {
    return millis2FitTimeSpan(abs(beginMillis - endMillis), unit)
}

private val UNITS = intArrayOf(DAY, HOUR, MINUTE, SECOND, MILLISECOND)
private fun millis2FitTimeSpan(millis: Long, @Unit unit: Int): String {
    var precision = 4
    for (i in UNITS.indices) {
        if (unit == UNITS[i]) {
            precision = i
            break
        }
    }
    val units = arrayOf("天", "小时", "分钟", "秒", "毫秒")
    if (millis == 0L) {
        return "0" + units[precision]
    }
    val sb = StringBuilder()
    for (i in 0 until precision + 1) {
        if (millis >= UNITS[i]) {
            val mode = millis / UNITS[i]
            millis.minus(mode * UNITS[i])
            sb.append(mode).append(units[i])
        }
    }
    return sb.toString()
}

/**
 * 获取指定日期与当前时间间隔状态
 *
 * @param date   日期
 * @param format 日期格式
 *
 * @return 时间间隔状态
 *
 *  * 如果小于 1 秒钟内，显示刚刚
 *  * 如果在 1 分钟内，显示 XXX秒前
 *  * 如果在 1 小时内，显示 XXX分钟前
 *  * 如果在 1 小时外的今天内，显示今天hh:mm
 *  * 如果是昨天的，显示昨天hh:mm
 *  * 其余显示，yyyy-MM-dd
 *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
 *
 */
fun dateSpanStatusToNow(date: String, format: String = DEFAULT_FORMAT): String {
    return timeSpanStatusToNow(dateToMillis(date, format))
}

/**
 * 获取指定时间与当前时间间隔状态
 *
 * @param millis 毫秒时间戳
 *
 * @return 时间间隔状态
 *
 *  * 如果小于 1 秒钟内，显示刚刚
 *  * 如果在 1 分钟内，显示 XXX秒前
 *  * 如果在 1 小时内，显示 XXX分钟前
 *  * 如果在 1 小时外的今天内，显示今天15:32
 *  * 如果是昨天的，显示昨天hh:mm
 *  * 其余显示，yyyy-MM-dd
 *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
 *
 */
fun timeSpanStatusToNow(millis: Long): String {
    val now = System.currentTimeMillis()
    val span = now - millis
    if (span < 0) {
        return String.format("%tc", millis)
    }
    when {
        span < 1000 -> {
            return "刚刚"
        }
        span < MINUTE -> {
            return String.format(Locale.getDefault(), "%d秒前", span / SECOND)
        }
        span < HOUR -> {
            return String.format(Locale.getDefault(), "%d分钟前", span / MINUTE)
        }
        // 获取当天 00:00
        else -> {
            val wee = todayZero
            return when {
                millis >= wee -> {
                    String.format("今天%tR", millis)
                }
                millis >= wee - DAY -> {
                    String.format("昨天%tR", millis)
                }
                else -> {
                    String.format("%tF", millis)
                }
            }
        }
    }
}

/**
 * 判断指定日期是否是今天
 *
 * 默认日期格式 yyyy-MM-dd HH:mm:ss
 *
 * @param date   字符串日期
 * @param format 日期格式
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun isToday(date: String, format: String = DEFAULT_FORMAT): Boolean {
    return isToday(dateToMillis(date, format), MILLISECOND)
}

/**
 * 判断指定日期是否是今天
 *
 * @param date 日期
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun isToday(date: Date): Boolean {
    return isToday(date.time, MILLISECOND)
}

/**
 * 判断指定时间是否是今天
 *
 * @param time 时间戳
 * @param unit 时间戳单位
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun isToday(time: Long, @Unit unit: Int): Boolean {
    val millis = time * unit
    val wee = todayZero
    return millis >= wee && millis < wee + DAY
}

/**
 * 是否是闰年
 *
 * 默认日期格式 yyyy-MM-dd HH:mm:ss
 *
 * @param date   字符串日期
 * @param format 日期格式
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun isLeapYear(date: String, format: String = DEFAULT_FORMAT): Boolean {
    return isLeapYear(dateToMillis(date, format), MILLISECOND)
}

/**
 * 是否是闰年
 *
 * 默认日期格式 yyyy-MM-dd HH:mm:ss
 *
 * @param date 日期
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun isLeapYear(date: Date): Boolean {
    return isLeapYear(date.time, MILLISECOND)
}

/**
 * 是否是闰年
 *
 * @param time 时间戳
 * @param unit 时间戳单位
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun isLeapYear(time: Long, @Unit unit: Int): Boolean {
    val cal = Calendar.getInstance()
    cal.timeInMillis = time * unit
    val year = cal[Calendar.YEAR]
    return isLeapYear(year)
}

/**
 * 是否是闰年
 *
 * @param year 年份
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && year % 100 != 0 || year % 400 == 0
}

/**
 * 获取指定日期为周几
 *
 * @param date   字符串日期
 * @param format 日期格式
 *
 * @return 周几
 */
fun getWeek(date: String, format: String = DEFAULT_FORMAT): String {
    return getWeek(dateToMillis(date, format), MILLISECOND)
}

/**
 * 获取指定日期为周几
 *
 * @param date   字符串日期
 *
 * @return 周几
 */
fun getWeek(date: Date): String {
    return getWeek(date.time, MILLISECOND)
}

/**
 * 获取指定时间为周几
 *
 * @param time 时间戳
 * @param unit 时间戳单位
 *
 * @return 周几
 */
fun getWeek(time: Long, @Unit unit: Int): String {
    return SimpleDateFormat("E", Locale.getDefault()).format(Date(time * unit))
}

val todayZero: Long
    get() {
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.timeInMillis
    }

/**
 * 获取当月天数
 *
 * @return 当月天数
 */
val currentMonthDayCount: Int
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        //获取指定年份中指定月份有几天
        return calendar.getActualMaximum(Calendar.DATE)
    }

/**
 * 获取当月第一天的时间戳
 *
 * 0时0分0秒
 *
 * @return 当月第一天的时间戳
 */
val currentMonthFirstDayTime: Long
    get() {
        // 获取当前日期
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        // 设置为1号,当前日期既为本月第一天
        calendar[Calendar.DAY_OF_MONTH] = 1
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        return calendar.timeInMillis
    }

/**
 * 获取当月最后一天的时间戳
 *
 * 23时59分59秒
 *
 * @return 当月最后一天的时间戳
 */
val currentMonthLastDayTime: Long
    get() {
        // 获取当前日期
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMaximum(Calendar.DATE)
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
        calendar[Calendar.SECOND] = 59
        return calendar.timeInMillis
    }

/**************************************** 十二星座 ****************************************/
private val CONSTELLATIONS_FLAGS =
    intArrayOf(20, 19, 21, 21, 21, 22, 23, 23, 23, 24, 23, 22)
private val CONSTELLATIONS = arrayOf(
    "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
    "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座"
)

/**
 * 获取指定日期的星座
 *
 * @param date   字符串日期
 * @param format 日期格式
 *
 * @return 星座 [CONSTELLATIONS]
 */
fun getConstellations(date: String, format: String = DEFAULT_FORMAT): String {
    return getConstellations(dateToMillis(date, format), MILLISECOND)
}

/**
 * 获取星座
 *
 * @param time 时间戳
 * @param unit 时间戳单位
 *
 * @return 星座 [CONSTELLATIONS]
 */
fun getConstellations(time: Long, @Unit unit: Int): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = time * unit
    val month = cal[Calendar.MONTH] + 1
    val day = cal[Calendar.DAY_OF_MONTH]
    return getConstellations(month, day)
}

/**
 * 获取星座
 *
 * @param month 月份
 * @param day   日
 *
 * @return 星座 [CONSTELLATIONS]
 */
fun getConstellations(month: Int, day: Int): String {
    return CONSTELLATIONS[if (day >= CONSTELLATIONS_FLAGS[month - 1]) month - 1 else (month + 10) % 12]
}