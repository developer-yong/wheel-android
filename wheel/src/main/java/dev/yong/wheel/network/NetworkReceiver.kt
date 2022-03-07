package dev.yong.wheel.network;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

/**
 * 网络广播接收器
 *
 * @author CoderYong
 */
@SuppressLint("MissingPermission")
public class NetworkReceiver extends BroadcastReceiver {

    private OnNetworkListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            int type = Network.getNetworkType(context);
            // 接口回调传过去状态的类型
            if (mListener != null) {
                mListener.onNetworkChange(type);
            }
        }
    }

    public static NetworkReceiver register(Context context, OnNetworkListener listener) {
        NetworkReceiver receiver = new NetworkReceiver();
        context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        receiver.setOnNetworkListener(listener);
        return receiver;
    }

    public void setOnNetworkListener(OnNetworkListener listener) {
        mListener = listener;
    }

    public interface OnNetworkListener {

        /**
         * 网络状态改变回调执行方法
         *
         * @param type 网络连接类型
         */
        void onNetworkChange(int type);
    }
}
