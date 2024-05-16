package dev.yong.wheel.oaid.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import dev.yong.wheel.oaid.IGatherCallback;
import dev.yong.wheel.oaid.OAIDException;
import dev.yong.wheel.oaid.OAIDLog;
import dev.yong.wheel.oaid.OAIDService;
import dev.yong.wheel.oaid.aidl.IStdID;

/**
 * Oppo手机OAID采集实现，参阅 com.umeng.umsdk:oaid_oppo:1.0.4
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class OppoExtGathererImpl extends OppoGathererImpl {

    private static final String TARGET_PKG = "com.coloros.mcs";
    private static final String SERVICE_NAME = "com.oplus.stdid.IdentifyService";
    private static final String INTENT_ACTION = "action.com.oplus.stdid.ID_SERVICE";

    private final Context mContext;

    public OppoExtGathererImpl(Context context) {
        super(context);
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
        intent.setComponent(new ComponentName(TARGET_PKG, SERVICE_NAME));
        OAIDService.bind(mContext, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                try {
                    return realGetOUID(service);
                } catch (OAIDException | RemoteException e) {
                    throw e;
                } catch (Exception e) {
                    throw new OAIDException(e);
                }
            }
        });
    }

    protected String getSerId(IBinder service, String pkgName, String sign) throws RemoteException, OAIDException {
        IStdID anInterface = IStdID.Stub.asInterface(service);
        if (anInterface == null) {
            throw new OAIDException("IStdID is null");
        }
        return anInterface.getSerID(pkgName, sign, "OUID");
    }
}
