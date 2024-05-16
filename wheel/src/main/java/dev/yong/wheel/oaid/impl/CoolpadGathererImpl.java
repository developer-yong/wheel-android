package dev.yong.wheel.oaid.impl;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import dev.yong.wheel.oaid.IGatherCallback;
import dev.yong.wheel.oaid.IGatherer;
import dev.yong.wheel.oaid.OAIDException;
import dev.yong.wheel.oaid.OAIDLog;
import dev.yong.wheel.oaid.OAIDService;
import dev.yong.wheel.oaid.aidl.IDeviceIdManager;

/**
 * 酷派手机OAID采集实现
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class CoolpadGathererImpl implements IGatherer {

    private static final String TARGET_PKG = "com.coolpad.deviceidsupport";
    private static final String SERVICE_NAME = "com.coolpad.deviceidsupport.DeviceIdService";

    private final Context mContext;

    public CoolpadGathererImpl(Context context) {
        if (context instanceof Application) {
            this.mContext = context;
        } else {
            this.mContext = context.getApplicationContext();
        }
    }

    @Override
    public boolean isSupported() {
        if (mContext == null) {
            return false;
        }
        try {
            return mContext.getPackageManager().getPackageInfo(TARGET_PKG, 0) != null;
        } catch (Exception e) {
            OAIDLog.print(e);
            return false;
        }
    }

    @Override
    public void doGather(IGatherCallback getter) {
        if (mContext == null || getter == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(TARGET_PKG, SERVICE_NAME));
        OAIDService.bind(mContext, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                IDeviceIdManager anInterface = IDeviceIdManager.Stub.asInterface(service);
                if (anInterface == null) {
                    throw new OAIDException("IDeviceIdManager is null");
                }
                return anInterface.getOAID(mContext.getPackageName());
            }
        });
    }
}
