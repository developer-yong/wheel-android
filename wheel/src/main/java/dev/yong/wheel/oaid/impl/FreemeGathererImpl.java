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
import dev.yong.wheel.oaid.aidl.IdsSupplier;

/**
 * 卓易手机OAID采集实现
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class FreemeGathererImpl implements IGatherer {

    private static final String TARGET_PKG = "com.android.creator";
    private static final String AIDL_ACTION = "android.service.action.msa";

    private final Context mContext;

    public FreemeGathererImpl(Context context) {
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
        Intent intent = new Intent(AIDL_ACTION);
        intent.setPackage(TARGET_PKG);
        OAIDService.bind(mContext, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                IdsSupplier anInterface = IdsSupplier.Stub.asInterface(service);
                if (anInterface == null) {
                    throw new OAIDException("IdsSupplier is null");
                }
                return anInterface.getOAID();
            }
        });
    }
}
