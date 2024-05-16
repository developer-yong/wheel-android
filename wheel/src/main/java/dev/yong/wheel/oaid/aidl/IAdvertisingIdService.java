package dev.yong.wheel.oaid.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 本文件代码根据以下AIDL生成，只改包名以便解决和移动安全联盟的SDK冲突问题：
 * <pre>
 *     // IAdvertisingIdService.aidl
 *     package com.google.android.gms.ads.identifier.internal;
 *
 *     interface IAdvertisingIdService {
 *
 *         String getId();
 *
 *         boolean isLimitAdTrackingEnabled(boolean boo);
 *
 *     }
 * </pre>
 */
public interface IAdvertisingIdService extends IInterface {

    /**
     * Local-side IPC implementation stub class.
     */
    abstract class Stub extends Binder implements IAdvertisingIdService {

        private static final String DESCRIPTOR = "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService";

        static final int TRANSACTION_getId = IBinder.FIRST_CALL_TRANSACTION;
        static final int TRANSACTION_isLimitAdTrackingEnabled = IBinder.FIRST_CALL_TRANSACTION + 1;

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an repeackage.com.google.android.gms.ads.identifier.internal.IAdvertisingIdService interface,
         * generating a proxy if needed.
         */
        public static IAdvertisingIdService asInterface(IBinder service) {
            if (service == null) {
                return null;
            }
            IInterface iInterface = service.queryLocalInterface(DESCRIPTOR);
            if (iInterface instanceof IAdvertisingIdService) {
                return (IAdvertisingIdService) iInterface;
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
                case TRANSACTION_getId: {
                    data.enforceInterface(descriptor);
                    String result = this.getId();
                    reply.writeNoException();
                    reply.writeString(result);
                    return true;
                }
                case TRANSACTION_isLimitAdTrackingEnabled: {
                    data.enforceInterface(descriptor);
                    boolean result = this.isLimitAdTrackingEnabled(0 != data.readInt());
                    reply.writeNoException();
                    reply.writeInt(result ? 1 : 0);
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements IAdvertisingIdService {

            private final IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            @Override
            public String getId() throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                String result;
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getId, data, reply, 0);
                    reply.readException();
                    result = reply.readString();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
                return result;
            }

            @Override
            public boolean isLimitAdTrackingEnabled(boolean boo) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                boolean result;
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeInt(boo ? 1 : 0);
                    mRemote.transact(Stub.TRANSACTION_isLimitAdTrackingEnabled, data, reply, 0);
                    reply.readException();
                    result = 0 != reply.readInt();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
                return result;
            }
        }
    }

    String getId() throws RemoteException;

    boolean isLimitAdTrackingEnabled(boolean boo) throws RemoteException;
}
