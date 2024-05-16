package dev.yong.wheel.oaid.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;

import dev.yong.wheel.oaid.IGatherCallback;
import dev.yong.wheel.oaid.IGatherer;
import dev.yong.wheel.oaid.OAIDException;
import dev.yong.wheel.oaid.OAIDLog;
import dev.yong.wheel.oaid.OAIDService;
import dev.yong.wheel.oaid.aidl.MsaIdInterface;

/**
 * Msa OAID采集实现
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class MsaGathererImpl implements IGatherer {

    private static final String TARGET_PKG = "com.mdid.msa";
    private static final String SERVICE_NAME = "com.mdid.msa.service.MsaIdService";
    private static final String INTENT_ACTION = "com.bun.msa.action.bindto.service";
    private static final String INTENT_EXTRA_PKG = "com.bun.msa.param.pkgname";

    private final Context mContext;

    public MsaGathererImpl(Context context) {
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
        startMsaKlService();
        Intent intent = new Intent(INTENT_ACTION);
        intent.setClassName(TARGET_PKG, SERVICE_NAME);
        intent.putExtra(INTENT_EXTRA_PKG, mContext.getPackageName());
        OAIDService.bind(mContext, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                MsaIdInterface anInterface = MsaIdInterface.Stub.asInterface(service);
                if (anInterface == null) {
                    throw new OAIDException("MsaIdInterface is null");
                }
                if (!anInterface.isSupported()) {
                    throw new OAIDException("MsaIdInterface#isSupported return false");
                }
                return anInterface.getOAID();
            }
        });
    }

    private void startMsaKlService() {
        try {
            Intent intent = new Intent(INTENT_ACTION);
            intent.setClassName(TARGET_PKG, SERVICE_NAME);
            intent.putExtra(INTENT_EXTRA_PKG, mContext.getPackageName());
            if (Build.VERSION.SDK_INT < 26) {
                mContext.startService(intent);
            } else {
                mContext.startForegroundService(intent);
            }
        } catch (Exception e) {
            OAIDLog.print(e);
        }
    }
}
