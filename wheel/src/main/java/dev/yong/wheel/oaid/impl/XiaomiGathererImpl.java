package dev.yong.wheel.oaid.impl;

import android.annotation.SuppressLint;
import android.content.Context;

import dev.yong.wheel.oaid.IGatherCallback;
import dev.yong.wheel.oaid.IGatherer;
import dev.yong.wheel.oaid.OAIDException;
import dev.yong.wheel.oaid.OAIDLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 小米手机OAID采集实现
 * <p>
 * 参阅 <a href="http://f4.market.xiaomi.com/download/MiPass/058fc4374ac89aea6dedd9dc03c60a5498241e0dd/DeviceId.jar">DeviceId.jar</a>
 * 即 com.miui.deviceid.IdentifierManager
 * </P>
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
@SuppressWarnings("All")
public class XiaomiGathererImpl implements IGatherer {

    private final Context mContext;
    private Class<?> mIDProviderClass;
    private Object mIDProviderImpl;

    @SuppressLint("PrivateApi")
    public XiaomiGathererImpl(Context context) {
        this.mContext = context;
        try {
            mIDProviderClass = Class.forName("com.android.id.impl.IdProviderImpl");
            mIDProviderImpl = mIDProviderClass.newInstance();
        } catch (Exception e) {
            OAIDLog.print(e);
        }
    }

    @Override
    public boolean isSupported() {
        return mIDProviderImpl != null;
    }

    @Override
    public void doGather(final IGatherCallback getter) {
        if (mContext == null || getter == null) {
            return;
        }
        if (mIDProviderClass == null || mIDProviderImpl == null) {
            getter.onError(new OAIDException("Xiaomi IdProvider not exists"));
            return;
        }
        try {
            String oaid = getOAID();
            if (oaid == null || oaid.length() == 0) {
                throw new OAIDException("OAID query failed");
            }
            OAIDLog.print("OAID query success: " + oaid);
            getter.onSuccessful(oaid);
        } catch (Exception e) {
            OAIDLog.print(e);
            getter.onError(e);
        }
    }

    private String getOAID() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = mIDProviderClass.getMethod("getOAID", Context.class);
        return (String) method.invoke(mIDProviderImpl, mContext);
    }
}
