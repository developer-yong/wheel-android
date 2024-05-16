package dev.yong.wheel.oaid.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 本文件代码根据以下AIDL生成，只改包名以便解决和移动安全联盟的SDK冲突问题：
 * <pre>
 *     //IDeviceIdManager.aidl
 *     package com.coolpad.deviceidsupport;
 *
 *     interface IDeviceIdManager {
 *
 *         String getUDID(String str);
 *
 *         String getOAID(String str);
 *
 *         String getVAID(String str);
 *
 *         String getAAID(String str);
 *
 *         String getIMEI(String str);
 *
 *         boolean isCoolOs();
 *
 *         String getCoolOsVersion();
 *
 *     }
 * </pre>
 */
public interface IDeviceIdManager extends IInterface {

    /**
     * Local-side IPC implementation stub class.
     */
    abstract class Stub extends Binder implements IDeviceIdManager {

        private static final String DESCRIPTOR = "com.coolpad.deviceidsupport.IDeviceIdManager";

        static final int TRANSACTION_getOAID = IBinder.FIRST_CALL_TRANSACTION + 1;

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an repeackage.com.coolpad.deviceidsupport.IDeviceIdManager interface,
         * generating a proxy if needed.
         */
        public static IDeviceIdManager asInterface(IBinder service) {
            if (service == null) {
                return null;
            }
            IInterface iInterface = service.queryLocalInterface(DESCRIPTOR);
            if (iInterface instanceof IDeviceIdManager) {
                return (IDeviceIdManager) iInterface;
            }
            return new Proxy(service);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_getOAID: {
                    data.enforceInterface(descriptor);
                    String result = this.getOAID(data.readString());
                    reply.writeNoException();
                    reply.writeString(result);
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements IDeviceIdManager {

            private final IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            @Override
            public String getOAID(String str) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                String result;
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeString(str);
                    mRemote.transact(Stub.TRANSACTION_getOAID, data, reply, 0);
                    reply.readException();
                    result = reply.readString();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
                return result;
            }
        }
    }

    String getOAID(String str) throws RemoteException;
}
