package dev.yong.wheel.oaid.impl;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import dev.yong.wheel.oaid.IGatherCallback;
import dev.yong.wheel.oaid.IGatherer;
import dev.yong.wheel.oaid.OAIDException;
import dev.yong.wheel.oaid.OAIDLog;

/**
 * 魅族手机OAID采集实现
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class MeizuGathererImpl implements IGatherer {

    private static final String TARGET_PKG = "com.meizu.flyme.openidsdk";
    private static final String URI = "content://com.meizu.flyme.openidsdk/";

    private final Context mContext;

    public MeizuGathererImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public boolean isSupported() {
        if (mContext == null) {
            return false;
        }
        try {
            return mContext.getPackageManager().resolveContentProvider(TARGET_PKG, 0) != null;
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
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(
                    Uri.parse(URI), null, null, new String[]{"oaid"}, null);
            cursor.moveToFirst();
            String oaid = cursor.getString(cursor.getColumnIndex("value"));
            if (oaid == null || oaid.length() == 0) {
                throw new OAIDException("OAID query failed");
            }
            OAIDLog.print("OAID query success: " + oaid);
            getter.onSuccessful(oaid);
        } catch (Exception e) {
            OAIDLog.print(e);
            getter.onError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }
}
