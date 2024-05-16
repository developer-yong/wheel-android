package dev.yong.wheel.oaid.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 本文件代码根据以下AIDL生成，只改包名以便解决和移动安全联盟的SDK冲突问题：
 * <pre>
 *     // IdsSupplier.aidl
 *     package com.android.creator;
 *
 *     interface IdsSupplier {
 *
 *        boolean isSupported();
 *
 *        String getUDID(String str);
 *
 *        String getOAID();
 *
 *        String getVAID();
 *
 *         String getAAID(String str);
 *
 *     }
 * </pre>
 */
public interface IdsSupplier extends IInterface {

    /**
     * Local-side IPC implementation stub class.
     */
    abstract class Stub extends Binder implements IdsSupplier {

        private static final String DESCRIPTOR = "com.android.creator.IdsSupplier";

        static final int TRANSACTION_isSupported = IBinder.FIRST_CALL_TRANSACTION;
        static final int TRANSACTION_getOAID = IBinder.FIRST_CALL_TRANSACTION + 2;

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an repeackage.com.android.creator.IdsSupplier interface,
         * generating a proxy if needed.
         */
        public static IdsSupplier asInterface(IBinder service) {
            if (service == null) {
                return null;
            }
            IInterface iInterface = service.queryLocalInterface(DESCRIPTOR);
            if (iInterface instanceof IdsSupplier) {
                return (IdsSupplier) iInterface;
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
                case TRANSACTION_isSupported: {
                    data.enforceInterface(descriptor);
                    boolean result = this.isSupported();
                    reply.writeNoException();
                    reply.writeInt(result ? 1 : 0);
                    return true;
                }
                case TRANSACTION_getOAID: {
                    data.enforceInterface(descriptor);
                    String result = this.getOAID();
                    reply.writeNoException();
                    reply.writeString(result);
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements IdsSupplier {

            private final IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            @Override
            public boolean isSupported() throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                boolean result;
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_isSupported, data, reply, 0);
                    reply.readException();
                    result = 0 != reply.readInt();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
                return result;
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

    boolean isSupported() throws RemoteException;

    String getOAID() throws RemoteException;
}
