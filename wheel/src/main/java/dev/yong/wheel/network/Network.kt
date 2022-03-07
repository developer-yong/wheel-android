package dev.yong.wheel.network

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

const val NETWORK_WIFI = 1
const val NETWORK_4G = 4
const val NETWORK_3G = 3
const val NETWORK_2G = 2
const val NETWORK_UNKNOWN = 5
const val NETWORK_NO = -1
private const val NETWORK_TYPE_GSM = 16
private const val NETWORK_TYPE_TD_SCDMA = 17
private const val NETWORK_TYPE_IWLAN = 18

/**
 * 网络相关工具类
 *
 * @author coderyong
 */
object Network {

    /**
     * 打开网络设置界面
     *
     * @param context 上下文
     */
    @JvmStatic
    fun openWirelessSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_SETTINGS))
    }

    /**
     * 获取活动网络信息
     *
     * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
     *
     * @param context 上下文
     * @return NetworkInfo
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun getActiveNetworkInfo(context: Context): NetworkInfo? {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm?.activeNetworkInfo
    }

    /**
     * 判断网络是否可用
     *
     * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
     *
     * @param context 上下文
     * @return `true`: 可用<br></br>`false`: 不可用
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isAvailable(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && info.isAvailable
    }

    /**
     * 判断网络是否连接
     *
     * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
     *
     * @param context 上下文
     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isConnected(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && info.isConnected
    }

    /**
     * 判断网络是否是4G
     *
     * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
     *
     * @param context 上下文
     * @return `true`: 是<br></br>`false`: 不是
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun is4G(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && info.isAvailable && info.subtype == TelephonyManager.NETWORK_TYPE_LTE
    }

    /**
     * 判断wifi是否连接状态
     *
     * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
     *
     * @param context 上下文
     * @return `true`: 连接<br></br>`false`: 未连接
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isWifiConnected(context: Context): Boolean {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm != null && cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.type == ConnectivityManager.TYPE_WIFI
    }

    /**
     * 获取移动网络运营商名称
     *
     * 如中国联通、中国移动、中国电信
     *
     * @param context 上下文
     * @return 移动网络运营商名称
     */
    @JvmStatic
    fun getNetworkOperatorName(context: Context): String? {
        val tm = context
            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm?.networkOperatorName
    }

    /**
     * 获取移动终端类型
     *
     * @param context 上下文
     * @return 手机制式
     *
     *  * [TelephonyManager.PHONE_TYPE_NONE] : 0 手机制式未知
     *  * [TelephonyManager.PHONE_TYPE_GSM] : 1 手机制式为GSM，移动和联通
     *  * [TelephonyManager.PHONE_TYPE_CDMA] : 2 手机制式为CDMA，电信
     *  * [TelephonyManager.PHONE_TYPE_SIP] : 3
     *
     */
    @JvmStatic
    fun getPhoneType(context: Context): Int {
        val tm = context
            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm?.phoneType ?: -1
    }

    /**
     * 获取当前的网络类型(WIFI,2G,3G,4G)
     *
     * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
     *
     * @param context 上下文
     * @return 网络类型
     *
     *  * [.NETWORK_WIFI] = 1;
     *  * [.NETWORK_4G] = 4;
     *  * [.NETWORK_3G] = 3;
     *  * [.NETWORK_2G] = 2;
     *  * [.NETWORK_UNKNOWN] = 5;
     *  * [.NETWORK_NO] = -1;
     *
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetworkType(context: Context): Int {
        var netType = NETWORK_NO
        val info = getActiveNetworkInfo(context)
        if (info != null && info.isAvailable) {
            netType = if (info.type == ConnectivityManager.TYPE_WIFI) {
                NETWORK_WIFI
            } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                when (info.subtype) {
                    NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NETWORK_2G
                    NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NETWORK_3G
                    NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> NETWORK_4G
                    else -> {
                        val subtypeName = info.subtypeName
                        if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                            || subtypeName.equals("WCDMA", ignoreCase = true)
                            || subtypeName.equals("CDMA2000", ignoreCase = true)
                        ) {
                            NETWORK_3G
                        } else {
                            NETWORK_UNKNOWN
                        }
                    }
                }
            } else {
                NETWORK_UNKNOWN
            }
        }
        return netType
    }

    /**
     * 获取当前的网络类型(WIFI,2G,3G,4G)
     *
     * 依赖上面的方法
     *
     * @param context 上下文
     * @return 网络类型名称
     *
     *  * NETWORK_WIFI
     *  * NETWORK_4G
     *  * NETWORK_3G
     *  * NETWORK_2G
     *  * NETWORK_UNKNOWN
     *  * NETWORK_NO
     *
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetWorkTypeName(context: Context): String {
        return when (getNetworkType(context)) {
            NETWORK_WIFI -> "NETWORK_WIFI"
            NETWORK_4G -> "NETWORK_4G"
            NETWORK_3G -> "NETWORK_3G"
            NETWORK_2G -> "NETWORK_2G"
            NETWORK_NO -> "NETWORK_NO"
            else -> "NETWORK_UNKNOWN"
        }
    }

    /**
     * 回去IP地址
     *
     * @param context 上下文
     * @return IP地址
     */
    @SuppressLint("MissingPermission")
    @JvmStatic
    fun getIpAddress(context: Context): String {
        val info = (context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (info != null && info.isConnected) {
            if (info.type == ConnectivityManager.TYPE_MOBILE) {
                try {
                    val en = NetworkInterface.getNetworkInterfaces()
                    while (en.hasMoreElements()) {
                        val intf = en.nextElement()
                        val enumIpAddr = intf.inetAddresses
                        while (enumIpAddr.hasMoreElements()) {
                            val inetAddress = enumIpAddr.nextElement()
                            if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                return inetAddress.getHostAddress()
                            }
                        }
                    }
                } catch (e: SocketException) {
                    e.printStackTrace()
                }
            } else if (info.type == ConnectivityManager.TYPE_WIFI) {
                val ip = (context.applicationContext
                    .getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo.ipAddress
                return (ip and 0xFF).toString() + "." +
                        (ip shr 8 and 0xFF) + "." +
                        (ip shr 16 and 0xFF) + "." +
                        (ip shr 24 and 0xFF)
            }
        }
        return "0.0.0.0"
    }
}