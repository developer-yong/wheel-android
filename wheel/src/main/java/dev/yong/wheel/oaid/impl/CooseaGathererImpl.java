package dev.yong.wheel.oaid.impl;

import android.app.KeyguardManager;
import android.content.Context;

import dev.yong.wheel.oaid.IGatherCallback;
import dev.yong.wheel.oaid.IGatherer;
import dev.yong.wheel.oaid.OAIDException;
import dev.yong.wheel.oaid.OAIDLog;

/**
 * 酷赛手机OAID采集实现
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class CooseaGathererImpl implements IGatherer {

    private final Context mContext;
    private final KeyguardManager mKeyguardManager;

    public CooseaGathererImpl(Context context) {
        this.mContext = context;
        this.mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    }

    @Override
    public boolean isSupported() {
        if (mContext == null) {
            return false;
        }
        if (mKeyguardManager == null) {
            return false;
        }
        try {
            return (boolean) mKeyguardManager.getClass()
                    .getDeclaredMethod("isSupported").invoke(mKeyguardManager);
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
        if (mKeyguardManager == null) {
            getter.onError(new OAIDException("KeyguardManager not found"));
            return;
        }
        try {
            Object obj = mKeyguardManager.getClass().getDeclaredMethod("obtainOaid").invoke(mKeyguardManager);
            if (obj == null) {
                throw new OAIDException("OAID obtain failed");
            }
            String oaid = obj.toString();
            OAIDLog.print("OAID obtain success: " + oaid);
            getter.onSuccessful(oaid);
        } catch (Exception e) {
            OAIDLog.print(e);
        }
    }
}
