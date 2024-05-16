package dev.yong.wheel.oaid.impl;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import dev.yong.wheel.oaid.IGatherCallback;
import dev.yong.wheel.oaid.IGatherer;
import dev.yong.wheel.oaid.OAIDException;
import dev.yong.wheel.oaid.OAIDLog;
import dev.yong.wheel.oaid.ROM;

import java.util.Objects;

/**
 * Vivo手机OAID采集实现，参阅 com.umeng.umsdk:oaid_vivo:1.0.0.1
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class VivoGathererImpl implements IGatherer {

    private static final String PROPERTY = "persist.sys.identifierid.supported";
    private static final String URI = "content://com.vivo.vms.IdProvider/IdentifierId/OAID";

    private final Context mContext;

    public VivoGathererImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public boolean isSupported() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return false;
        }
        return ROM.sysProperty(PROPERTY, "0").equals("1");
    }

    @Override
    public void doGather(final IGatherCallback getter) {
        if (mContext == null || getter == null) {
            return;
        }
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver()
                    .query(Uri.parse(URI), null, null, null, null);
            Objects.requireNonNull(cursor).moveToFirst();
            String oaid = cursor.getString(cursor.getColumnIndex("value"));
            if (oaid == null || oaid.length() == 0) {
                throw new OAIDException("OAID query failed");
            }
            OAIDLog.print("OAID query success: " + oaid);
            getter.onSuccessful(oaid);
        } catch (Exception e) {
            OAIDLog.print(e);
            getter.onError(e);
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }
}
