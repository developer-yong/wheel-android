package dev.yong.wheel.oaid.impl;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import dev.yong.wheel.oaid.IGatherCallback;
import dev.yong.wheel.oaid.IGatherer;
import dev.yong.wheel.oaid.OAIDException;
import dev.yong.wheel.oaid.OAIDLog;

/**
 * 努比亚手机OAID采集实现
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class NubiaGathererImpl implements IGatherer {

    private static final String URI = "content://cn.nubia.identity/identity";

    private final Context mContext;

    public NubiaGathererImpl(Context context) {
        this.mContext = context;
    }

    @SuppressLint("AnnotateVersionCheck")
    @Override
    public boolean isSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    @Override
    public void doGather(IGatherCallback getter) {
        if (mContext == null || getter == null) {
            return;
        }
        if (!isSupported()) {
            String message = "Only supports Android 10.0 and above for Nubia";
            OAIDLog.print(message);
            getter.onError(new OAIDException(message));
            return;
        }
        String oaid = null;
        try {
            ContentProviderClient client = mContext.getContentResolver().acquireContentProviderClient(Uri.parse(URI));
            if (client == null) {
                return;
            }
            Bundle bundle = client.call("getOAID", null, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                client.close();
            } else {
                client.release();
            }
            if (bundle == null) {
                throw new OAIDException("OAID query failed: bundle is null");
            }
            if (bundle.getInt("code", -1) == 0) {
                oaid = bundle.getString("id");
            }
            if (oaid == null || oaid.length() == 0) {
                throw new OAIDException("OAID query failed: " + bundle.getString("message"));
            }
            OAIDLog.print("OAID query success: " + oaid);
            getter.onSuccessful(oaid);
        } catch (Exception e) {
            OAIDLog.print(e);
            getter.onError(e);
        }
    }
}
