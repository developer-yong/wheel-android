package dev.yong.wheel.oaid.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 本文件代码根据以下AIDL生成，只改包名以便解决和移动安全联盟的SDK冲突问题：
 * <pre>
 *     // IOpenID.aidl
 *     package com.heytap.openid;
 *
 *     interface IOpenID {
 *
 *         String getSerID(String pkgName, String sign, String type);
 *
 *     }
 * </pre>
 */
public interface IOpenID extends IInterface {

    /**
     * Local-side IPC implementation stub class.
     */
    abstract class Stub extends Binder implements IOpenID {

        private static final String DESCRIPTOR = "com.heytap.openid.IOpenID";

        static final int TRANSACTION_getSerID = IBinder.FIRST_CALL_TRANSACTION;

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an repeackage.com.heytap.openid.IOpenID interface,
         * generating a proxy if needed.
         */
        public static IOpenID asInterface(IBinder service) {
            if (service == null) {
                return null;
            }
            IInterface iInterface = service.queryLocalInterface(DESCRIPTOR);
            if (iInterface instanceof IOpenID) {
                return (IOpenID) iInterface;
            }
            return new Proxy(service);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_getSerID: {
                    data.enforceInterface(DESCRIPTOR);
                    String pkgName = data.readString();
                    String sign = data.readString();
                    String type = data.readString();
                    String result = this.getSerID(pkgName, sign, type);
                    reply.writeNoException();
                    reply.writeString(result);
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements IOpenID {

            private final IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            @Override
            public String getSerID(String pkgName, String sign, String type) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                String result;
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeString(pkgName);
                    data.writeString(sign);
                    data.writeString(type);
                    mRemote.transact(Stub.TRANSACTION_getSerID, data, reply, 0);
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

    String getSerID(String pkgName, String sign, String type) throws RemoteException;
}
