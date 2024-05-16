package dev.yong.wheel.oaid.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 本文件代码根据以下AIDL生成，只改包名以便解决和移动安全联盟的SDK冲突问题：
 * <pre>
 *     // IDeviceIdService.aidl
 *     package com.samsung.android.deviceidservice;
 *
 *     interface IDeviceIdService {
 *
 *         String getOAID();
 *
 *         String getVAID(String str);
 *
 *         String getAAID(String str);
 *
 *     }
 * </pre>
 */
public interface IDeviceIdService extends IInterface {

    /**
     * Local-side IPC implementation stub class.
     */
    abstract class Stub extends Binder implements IDeviceIdService {

        private static final String DESCRIPTOR = "com.samsung.android.deviceidservice.IDeviceIdService";

        static final int TRANSACTION_getOAID = IBinder.FIRST_CALL_TRANSACTION;

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an repeackage.com.samsung.android.deviceidservice.IDeviceIdService interface,
         * generating a proxy if needed.
         */
        public static IDeviceIdService asInterface(IBinder service) {
            if ((service == null)) {
                return null;
            }
            IInterface iInterface = service.queryLocalInterface(DESCRIPTOR);
            if (iInterface instanceof IDeviceIdService) {
                return (IDeviceIdService) iInterface;
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
                    String oaid = getOAID();
                    reply.writeNoException();
                    reply.writeString(oaid);
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements IDeviceIdService {

            private final IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            @Override
            public String getOAID() throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                String result;
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
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

    String getOAID() throws RemoteException;
}
