package dev.yong.wheel.oaid.impl;

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
import dev.yong.wheel.oaid.aidl.IDidAidlInterface;

/**
 * 华硕手机OAID采集实现
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class AsusGathererImpl implements IGatherer {

    private static final String TARGET_PKG = "com.asus.msa.SupplementaryDID";
    private static final String SERVICE_NAME = "com.asus.msa.SupplementaryDID.SupplementaryDIDService";
    private static final String INTENT_ACTION = "com.asus.msa.action.ACCESS_DID";

    private final Context mContext;

    public AsusGathererImpl(Context context) {
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
        Intent intent = new Intent(INTENT_ACTION);
        ComponentName componentName = new ComponentName(TARGET_PKG, SERVICE_NAME);
        intent.setComponent(componentName);
        OAIDService.bind(mContext, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                IDidAidlInterface anInterface = IDidAidlInterface.Stub.asInterface(service);
                if (anInterface == null) {
                    throw new OAIDException("IDidAidlInterface is null");
                }
                if (!anInterface.isSupport()) {
                    throw new OAIDException("IDidAidlInterface#isSupport return false");
                }
                return anInterface.getOAID();
            }
        });
    }
}
