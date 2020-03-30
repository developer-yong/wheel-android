package dev.yong.wheel.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.RequiresPermission;

/**
 * @author coderyong
 */
public class Bluetooth {
    public static final int REQUEST_BLUETOOTH = 0x01;

    private Bluetooth() {
        throw new UnsupportedOperationException("Cannot be created");
    }

    /**
     * 当前设备是否支持 Bluetooth
     *
     * @return true：支持 Bluetooth false：不支持 Bluetooth
     */
    public static boolean isSupported() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }


    /**
     * 当前设备的 bluetooth 是否已经开启
     *
     * @return true：Bluetooth 已经开启 false：Bluetooth 未开启
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public static boolean isEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }


    /**
     * 强制开启当前设备的 Bluetooth
     *
     * @return true：强制打开 Bluetooth　成功　false：强制打开 Bluetooth 失败
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static boolean open() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.enable();
    }

    /**
     * 弹出系统弹框提示用户打开 Bluetooth
     */
    public static void open(Activity activity) {
        // 请求打开 Bluetooth
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intent.setClass(activity, activity.getClass());
        // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
        intent.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // 设置 Bluetooth 设备可见时间(默认为120秒)
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
        activity.startActivityForResult(intent, REQUEST_BLUETOOTH);
    }

    /**
     * 关闭当前设备的 Bluetooth
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static void close() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.disable();
        }
    }

    /**
     * 启动系统蓝牙设置
     *
     * @param context 调用的context
     */
    public static void startSetting(Context context) {
        // 跳转到系统 Bluetooth 设置
        context.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
    }
}
