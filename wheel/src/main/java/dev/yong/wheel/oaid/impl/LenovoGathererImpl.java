package dev.yong.wheel.oaid.impl;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import dev.yong.wheel.oaid.IGatherCallback;
import dev.yong.wheel.oaid.IGatherer;
import dev.yong.wheel.oaid.OAIDException;
import dev.yong.wheel.oaid.OAIDLog;
import dev.yong.wheel.oaid.OAIDService;
import dev.yong.wheel.oaid.aidl.IDeviceidInterface;

/**
 * 联想手机OAID采集实现，参阅 com.umeng.umsdk:oaid_lenovo:1.0.0
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class LenovoGathererImpl implements IGatherer {

    private static final String TARGET_PKG = "com.zui.deviceidservice";
    private static final String SERVICE_NAME = "com.zui.deviceidservice.DeviceidServic";
    private static final String INTENT_ACTION = "com.asus.msa.action.ACCESS_DID";

    private final Context mContext;

    public LenovoGathererImpl(Context context) {
        this.mContext = context;
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
        intent.setClassName(TARGET_PKG, SERVICE_NAME);
        OAIDService.bind(mContext, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                IDeviceidInterface anInterface = IDeviceidInterface.Stub.asInterface(service);
                if (anInterface == null) {
                    throw new OAIDException("IDeviceidInterface is null");
                }
                if (!anInterface.isSupport()) {
                    throw new OAIDException("IDeviceidInterface#isSupport return false");
                }
                return anInterface.getOAID();
            }
        });
    }
}
