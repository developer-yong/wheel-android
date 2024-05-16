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
import dev.yong.wheel.oaid.aidl.IAdvertisingIdService;

/**
 * 参阅谷歌官方 Google Play Services SDK。
 * <p>
 * implementation `com.google.android.gms:play-services-ads:19.4.0`
 * AdvertisingIdClient.getAdvertisingIdInfo(context).getId()
 * </P>
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class GmsGathererImpl implements IGatherer {

    private static final String TARGET_PKG = "com.android.vending";
    private static final String INTENT_ACTION = "com.google.android.gms.ads.identifier.service.START";
    private static final String INTENT_PKG = "com.google.android.gms";

    private final Context mContext;

    public GmsGathererImpl(Context context) {
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
        Intent intent = new Intent(INTENT_PKG);
        intent.setPackage(INTENT_PKG);
        OAIDService.bind(mContext, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                IAdvertisingIdService anInterface = IAdvertisingIdService.Stub.asInterface(service);
                if (anInterface.isLimitAdTrackingEnabled(true)) {
                    // 实测在系统设置中停用了广告化功能也是能获取到广告标识符的
                    OAIDLog.print("User has disabled advertising identifier");
                }
                return anInterface.getId();
            }
        });
    }
}
