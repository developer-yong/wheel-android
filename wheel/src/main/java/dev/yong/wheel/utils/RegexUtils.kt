@file:Suppress("unused")

package dev.yong.wheel.utils

/**
 * 正则表达式：验证网页链接(http(s)://_ . _)
 */
val REGEX_LINK by lazy { Regex("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]") }

/**
 * 正则表达式：验证邮箱
 */
val REGEX_EMAIL by lazy { Regex("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$") }

/**
 * 正则表达式：验证中文字符
 */
val REGEX_CHINESE by lazy { Regex("[\\u4e00-\\u9fa5]") }

/**
 * 正则表达式：验证身份证
 */
val REGEX_ID_CARD by lazy { Regex("(^\\d{15}$)|(^\\d{17}[0-9Xx]$)") }

/**
 * 正则表达式：验证IP地址
 */
val REGEX_IP by lazy { Regex("^((0|[1]\\d{0,2}|[2][0-4]\\d|25[0-5])\\.){3}(0|[1]\\d{0,2}|[2][0-4]\\d|25[0-5])\$") }

/**
 * 正则表达式：验证正浮点数
 */
val REGEX_FLOAT by lazy { Regex("^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$") }

/**
 * 正则表达式：验证正整数
 */
val REGEX_INTEGER by lazy { Regex("^[1-9]\\d*|0$") }

/**
 * 正则验证
 *
 * @param regex 正则表达式
 * @return 是否匹配
 */
fun String.matches(regex: String): Boolean {
    return this.matches(Regex(regex))
}

/**
 * 是否是链接
 *
 * @return 是否匹配
 */
fun String.isLink(): Boolean {
    return this.matches(REGEX_LINK)
}


/**
 * 是否是邮箱
 *
 * @return 是否匹配
 */
fun String.isEmail(): Boolean {
    return this.matches(REGEX_EMAIL)
}


/**
 * 是否是中文
 *
 * @return 是否匹配
 */
fun String.isChinese(): Boolean {
    return this.matches(REGEX_CHINESE)
}

/**
 * 是否是身份证号
 *
 * @return 是否匹配
 */
fun String.isIdCard(): Boolean {
    return this.matches(REGEX_ID_CARD)
}

/**
 * 是否是IP地址
 *
 * @return 是否匹配
 */
fun String.isIp(): Boolean {
    return this.matches(REGEX_IP)
}

/**
 * 是否是正整数
 *
 * @return 是否匹配
 */
fun String.isInteger(): Boolean {
    return this.matches(REGEX_INTEGER)
}

/**
 * 是否是正浮点数
 *
 * @return 是否匹配
 */
fun String.isFloat(): Boolean {
    return this.matches(REGEX_FLOAT)
}

/**
 * 是否是正数（包括小数）
 * <P>int or float</P>
 *
 * @return 是否匹配
 */
fun String.isNumber(): Boolean {
    return this.matches(REGEX_INTEGER) || matches(REGEX_FLOAT)
}