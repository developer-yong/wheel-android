@file:Suppress("unused")

package dev.yong.wheel.utils

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.annotation.RequiresPermission

/**
 * @author coderyong
 */
object Bluetooth {

    private const val REQUEST_BLUETOOTH = 0x01

    /**
     * 当前设备是否支持 Bluetooth
     *
     * @return true：支持 Bluetooth false：不支持 Bluetooth
     */
    val isSupported: Boolean
        get() = BluetoothAdapter.getDefaultAdapter() != null

    /**
     * 当前设备的 bluetooth 是否已经开启
     *
     * @return true：Bluetooth 已经开启 false：Bluetooth 未开启
     */
    @get:RequiresPermission(Manifest.permission.BLUETOOTH)
    val isEnabled: Boolean
        get() {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            return bluetoothAdapter != null && bluetoothAdapter.isEnabled
        }

    /**
     * 强制开启当前设备的 Bluetooth
     *
     * @return true：强制打开 Bluetooth　成功　false：强制打开 Bluetooth 失败
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun open(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter != null && bluetoothAdapter.enable()
    }

    /**
     * 弹出系统弹框提示用户打开 Bluetooth
     */
    fun open(activity: Activity) {
        // 请求打开 Bluetooth
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        intent.setClass(activity, activity.javaClass)
        // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
        intent.action = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE
        // 设置 Bluetooth 设备可见时间(默认为120秒)
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120)
        activity.startActivityForResult(intent, REQUEST_BLUETOOTH)
    }

    /**
     * 关闭当前设备的 Bluetooth
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun close() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter?.disable()
    }

    /**
     * 启动系统蓝牙设置
     *
     * @param context 调用的context
     */
    fun startSetting(context: Context) {
        // 跳转到系统 Bluetooth 设置
        context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
    }
}