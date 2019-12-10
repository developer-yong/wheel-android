package dev.yong.wheel.utils;

import android.text.TextUtils;

/**
 * 正则工具类
 *
 * @author CoderYong
 */
public class RegexUtils {

    /**
     * 正则表达式：验证网页链接(http(s)://_ . _)
     */
    public static final String REGEX_LINK = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- \\./?%&#=]*)?";

    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$";

    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";

    /**
     * 正则表达式：验证中文字符
     */
    public static final String REGEX_CHINESE = "[\\u4e00-\\u9fa5]";

    /**
     * 正则表达式：验证身份证
     */
    public static final String REGEX_ID_CARD = "(^\\d{15}$)|(^\\d{17}[0-9Xx]$)";

    /**
     * 正则表达式：验证IP地址
     */
    public static final String REGEX_IP_ADDRESS = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

    /**
     * 正则表达式：验证正浮点数
     */
    public static final String REGEX_NUMBER_FLOAT = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
    /**
     * 正则表达式：验证非负整数
     */
    public static final String REGEX_NUMBER_INT = "^[1-9]\\d*|0$";

    private RegexUtils() {
        throw new UnsupportedOperationException("Cannot be created");
    }

    /**
     * 正则验证
     *
     * @param str   需要验证的字符串
     * @param regex 正则表达式
     * @return 是否匹配
     */
    public static boolean matches(String str, String regex) {
        return !TextUtils.isEmpty(str) && str.matches(regex);
    }

    /**
     * 是否是链接
     *
     * @param link 链接
     * @return 是否匹配
     */
    public static boolean isLink(String link) {
        return !TextUtils.isEmpty(link) && matches(link.trim(), REGEX_LINK);
    }

    /**
     * 是否是手机号
     *
     * @param mobile 手机号
     * @return 是否匹配
     */
    public static boolean isMobile(String mobile) {
        return !TextUtils.isEmpty(mobile) && matches(mobile.trim(), REGEX_MOBILE);
    }

    /**
     * 是否是邮箱
     *
     * @param email 邮箱
     * @return 是否匹配
     */
    public static boolean isEmail(String email) {
        return !TextUtils.isEmpty(email) && matches(email.trim(), REGEX_EMAIL);
    }

    /**
     * 是否是中文
     *
     * @param chinese 中文
     * @return 是否匹配
     */
    public static boolean isChinese(String chinese) {
        return !TextUtils.isEmpty(chinese) && matches(chinese.trim(), REGEX_CHINESE);
    }

    /**
     * 是否是身份证号
     *
     * @param idCard 身份证号
     * @return 是否匹配
     */
    public static boolean isIdCard(String idCard) {
        return !TextUtils.isEmpty(idCard) && matches(idCard.trim(), REGEX_ID_CARD);
    }

    /**
     * 是否是IP地址
     *
     * @param ip IP地址
     * @return 是否匹配
     */
    public static boolean isIp(String ip) {
        return !TextUtils.isEmpty(ip) && matches(ip.trim(), REGEX_IP_ADDRESS);
    }

    /**
     * 是否是数字
     * <P>int or float</P>
     *
     * @param number 数字字符串
     * @return 是否匹配
     */
    public static boolean isNumber(String number) {
        return !TextUtils.isEmpty(number) &&
                (matches(number.trim(), REGEX_NUMBER_INT) || matches(number, REGEX_NUMBER_FLOAT));
    }
}
