package dev.yong.wheel.oaid.impl;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;

import dev.yong.wheel.oaid.IGatherCallback;
import dev.yong.wheel.oaid.IGatherer;
import dev.yong.wheel.oaid.OAIDException;
import dev.yong.wheel.oaid.OAIDLog;
import dev.yong.wheel.oaid.OAIDService;
import dev.yong.wheel.oaid.aidl.OpenDeviceIdentifierService;

/**
 * 参阅华为官方 <a href="https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/identifier-service-integrating-sdk-0000001056460552">HUAWEI Ads SDK</a>。
 * 获取OAID信息（SDK方式） https://developer.huawei.com/consumer/cn/doc/HMSCore-Guides/identifier-service-obtaining-oaid-sdk-0000001050064988
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class HuaweiGathererImpl implements IGatherer {

    private static final String INTENT_ACTION = "com.uodis.opendevice.OPENIDS_SERVICE";
    private static final String URI_SCP = "content://com.huawei.hwid.pps.apiprovider/oaid_scp/get";
    private static final String URI_QUERY = "content://com.huawei.hwid.pps.apiprovider/oaid/query";

    private final Context mContext;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public HuaweiGathererImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public boolean isSupported() {
        if (mContext == null) {
            return false;
        }
        try {
            PackageManager pm = mContext.getPackageManager();
            String targetPkg = getTargetPkg(mContext);
            pm.getPackageInfo(targetPkg, 128);
            Intent intent = new Intent(INTENT_ACTION);
            intent.setPackage(targetPkg);
            if (!pm.queryIntentServices(intent, 0).isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            OAIDLog.print(e);
        }
        return false;
    }

    @Override
    public void doGather(IGatherCallback getter) {
        if (mContext == null || getter == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                String oaid = Settings.Global.getString(mContext.getContentResolver(), "pps_oaid");
                if (!TextUtils.isEmpty(oaid)
                        && !"00000000-0000-0000-0000-000000000000".equalsIgnoreCase(oaid)) {
                    getter.onSuccessful(oaid);
                    return;
                }
                String limit = Settings.Global.getString(
                        mContext.getContentResolver(), "pps_track_limit");
                if (Boolean.valueOf(limit)) {
                    getter.onError(new OAIDException("User has disabled advertising identifier"));
                    return;
                }
            } catch (Throwable e) {
                OAIDLog.print(e);
            }
        }
        if (checkCodeAndSignatures(mContext, Uri.parse(URI_SCP))) {
            String oaid = query(mContext);
            if (!TextUtils.isEmpty(oaid)) {
                getter.onSuccessful(oaid);
                return;
            }
        }
        Intent intent = new Intent(INTENT_ACTION);
        intent.setPackage(getTargetPkg(mContext));
        OAIDService.bind(mContext, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                OpenDeviceIdentifierService anInterface = OpenDeviceIdentifierService.Stub.asInterface(service);
                if (anInterface == null) {
                    throw new OAIDException("OpenDeviceIdentifierService is null");
                }
                if (anInterface.isOaidTrackLimited()) {
                    throw new OAIDException("OpenDeviceIdentifierService#isOaidTrackLimited return true");
                }
                return anInterface.getOaid();
            }
        });
    }

    public static String query(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    Uri.parse(URI_QUERY), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String oaid = cursor.getString(cursor.getColumnIndexOrThrow("oaid"));
                if (!TextUtils.isEmpty(oaid)
                        && !"00000000-0000-0000-0000-000000000000".equalsIgnoreCase(oaid)) {
                    return oaid;
                }
            }
        } catch (Throwable e) {
            OAIDLog.print(e);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return null;
    }

    private static String getTargetPkg(Context context) {
        if (checkPackageInfo(context, "com.huawei.hwid")) {
            return "com.huawei.hwid";
        } else if (checkPackageInfo(context, "com.huawei.hms")) {
            return "com.huawei.hms";
        } else {
            return checkPackageInfo(context, "com.huawei.hwid.tv") ? "com.huawei.hwid.tv" : "com.huawei.hwid";
        }
    }

    private static boolean checkPackageInfo(Context context, String pkg) {
        return getPackageInfo(context, pkg) != null;
    }

    private static PackageInfo getPackageInfo(Context context, String pkg) {
        if (context == null || TextUtils.isEmpty(pkg)) {
            return null;
        } else {
            try {
                return context.getPackageManager().getPackageInfo(pkg, 128);
            } catch (Exception e) {
                OAIDLog.print(e);
            }
            return null;
        }
    }

    private static boolean checkCodeAndSignatures(Context context, Uri uri) {
        if (null != context && null != uri) {
            Integer code = getPpsKitVerCode(context);
            return null != code && 30462100 <= code ? checkSignatures(context, uri) : false;
        } else {
            return false;
        }
    }

    private static boolean checkSignatures(Context context, Uri uri) {
        if (context != null && uri != null) {
            PackageManager pm = context.getPackageManager();
            ProviderInfo pInfo = pm.resolveContentProvider(uri.getAuthority(), 0);
            if (pInfo == null) {
                OAIDLog.print("StmUt verify provider invalid param");
                return false;
            } else {
                ApplicationInfo appInfo = pInfo.applicationInfo;
                if (null == appInfo) {
                    return false;
                } else {
                    String pkg = appInfo.packageName;
                    if (TextUtils.isEmpty(pkg)) {
                        return false;
                    } else {
                        return pm.checkSignatures(context.getPackageName(), pkg) == 0 || (appInfo.flags & 1) == 1;
                    }
                }
            }
        } else {
            return false;
        }
    }

    private static Integer getPpsKitVerCode(Context context) {
        if (null == context) {
            return null;
        } else {
            try {
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(getTargetPkg(context), 128);
                if (info != null && info.metaData != null) {
                    String code = info.metaData.get("ppskit_ver_code").toString();
                    if (code != null) {
                        return Integer.valueOf(code);
                    }
                }
            } catch (Throwable e) {
                OAIDLog.print(e);
            }
            return null;
        }
    }
}
