package dev.yong.wheel.oaid;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * 绑定远程的 OAID 服务
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
public class OAIDService implements ServiceConnection {

    private final Context mContext;
    private final IGatherCallback mCallback;
    private final RemoteCaller mCaller;

    public static void bind(Context context, Intent intent, IGatherCallback getter, RemoteCaller caller) {
        new OAIDService(context, getter, caller).bind(intent);
    }

    private OAIDService(Context context, IGatherCallback callback, RemoteCaller caller) {
        if (context instanceof Application) {
            this.mContext = context;
        } else {
            this.mContext = context.getApplicationContext();
        }
        this.mCallback = callback;
        this.mCaller = caller;
    }

    private void bind(Intent intent) {
        try {
            if (!mContext.bindService(intent, this, Context.BIND_AUTO_CREATE)) {
                throw new OAIDException("Service binding failed");
            }
            OAIDLog.print("Service has been bound: " + intent);
        } catch (Exception e) {
            mCallback.onError(e);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        OAIDLog.print("Service has been connected: " + name.getClassName());
        try {
            String oaid = mCaller.callRemoteInterface(service);
            if (oaid == null || oaid.length() == 0) {
                throw new OAIDException("OAID/AAID acquire failed");
            }
            OAIDLog.print("OAID/AAID acquire success: " + oaid);
            mCallback.onSuccessful(oaid);
        } catch (Exception e) {
            OAIDLog.print(e);
            mCallback.onError(e);
        } finally {
            try {
                mContext.unbindService(this);
                OAIDLog.print("Service has been unbound: " + name.getClassName());
            } catch (Exception e) {
                OAIDLog.print(e);
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        OAIDLog.print("Service has been disconnected: " + name.getClassName());
    }

    @FunctionalInterface
    public interface RemoteCaller {

        String callRemoteInterface(IBinder binder) throws OAIDException, RemoteException;
    }
}
