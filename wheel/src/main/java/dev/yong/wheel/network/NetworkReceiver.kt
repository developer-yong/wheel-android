package dev.yong.wheel.network

import android.content.Intent
import androidx.annotation.RequiresPermission
import android.net.NetworkInfo
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.net.wifi.WifiManager
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import dev.yong.wheel.network.NetworkReceiver.OnNetworkListener
import dev.yong.wheel.network.NetworkReceiver
import android.content.IntentFilter

/**
 * 网络广播接收器
 *
 * @author CoderYong
 */
@SuppressLint("MissingPermission")
class NetworkReceiver : BroadcastReceiver() {
    private var mListener: OnNetworkListener? = null
    override fun onReceive(context: Context, intent: Intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
            val type: Int = Network.getNetworkType(context)
            // 接口回调传过去状态的类型
            if (mListener != null) {
                mListener!!.onNetworkChange(type)
            }
        }
    }

    fun setOnNetworkListener(listener: OnNetworkListener?) {
        mListener = listener
    }

    interface OnNetworkListener {
        /**
         * 网络状态改变回调执行方法
         *
         * @param type 网络连接类型
         */
        fun onNetworkChange(type: Int)
    }

    companion object {
        fun register(context: Context, listener: OnNetworkListener?): NetworkReceiver {
            val receiver = NetworkReceiver()
            context.registerReceiver(
                receiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
            receiver.setOnNetworkListener(listener)
            return receiver
        }
    }
}