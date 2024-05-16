package dev.yong.wheel.oaid.impl;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.IBinder;
import android.os.RemoteException;

import dev.yong.wheel.oaid.IGatherCallback;
import dev.yong.wheel.oaid.IGatherer;
import dev.yong.wheel.oaid.OAIDException;
import dev.yong.wheel.oaid.OAIDLog;
import dev.yong.wheel.oaid.OAIDService;
import dev.yong.wheel.oaid.aidl.IOpenID;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Oppo手机OAID采集实现，参阅 com.umeng.umsdk:oaid_oppo:1.0.4
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class OppoGathererImpl implements IGatherer {

    private static final String TARGET_PKG = "com.heytap.openid";
    private static final String SERVICE_NAME = "com.heytap.openid.IdentifyService";
    private static final String INTENT_ACTION = "action.com.heytap.openid.OPEN_ID_SERVICE";

    private final Context mContext;
    private String mSign;

    public OppoGathererImpl(Context context) {
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

    @SuppressLint("PackageManagerGetSignatures")
    protected String realGetOUID(IBinder service) throws PackageManager.NameNotFoundException,
            NoSuchAlgorithmException, RemoteException, OAIDException {
        String pkgName = mContext.getPackageName();
        if (mSign == null) {
            Signature[] signatures = mContext.getPackageManager().getPackageInfo(pkgName,
                    PackageManager.GET_SIGNATURES).signatures;
            byte[] byteArray = signatures[0].toByteArray();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            byte[] digest = messageDigest.digest(byteArray);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(Integer.toHexString((b & 255) | 256).substring(1, 3));
            }
            mSign = sb.toString();
            return getSerId(service, pkgName, mSign);
        }
        return getSerId(service, pkgName, mSign);
    }

    protected String getSerId(IBinder service, String pkgName, String sign) throws RemoteException, OAIDException {
        IOpenID anInterface = IOpenID.Stub.asInterface(service);
        if (anInterface == null) {
            throw new OAIDException("IOpenID is null");
        }
        return anInterface.getSerID(pkgName, sign, "OUID");
    }
}
