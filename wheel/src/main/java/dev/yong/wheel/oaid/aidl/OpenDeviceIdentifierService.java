package dev.yong.wheel.oaid.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 本文件代码根据以下AIDL生成，只改包名以便解决和移动安全联盟的SDK冲突问题：
 * <pre>
 *     // OpenDeviceIdentifierService.aidl
 *     package com.uodis.opendevice.aidl.OpenDeviceIdentifierService;
 *
 *     interface OpenDeviceIdentifierService {
 *
 *         String getOaid();
 *
 *         boolean isOaidTrackLimited();
 *
 *     }
 * </pre>
 */
public interface OpenDeviceIdentifierService extends IInterface {

    /**
     * Local-side IPC implementation stub class.
     */
    abstract class Stub extends Binder implements OpenDeviceIdentifierService {

        private static final String DESCRIPTOR = "com.uodis.opendevice.aidl.OpenDeviceIdentifierService";

        static final int TRANSACTION_getOAID = IBinder.FIRST_CALL_TRANSACTION;
        static final int TRANSACTION_isOaidTrackLimited = IBinder.FIRST_CALL_TRANSACTION + 1;

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static OpenDeviceIdentifierService asInterface(IBinder service) {
            if (service == null) {
                return null;
            }
            IInterface iInterface = service.queryLocalInterface(DESCRIPTOR);
            if (iInterface instanceof OpenDeviceIdentifierService) {
                return (OpenDeviceIdentifierService) iInterface;
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
                case TRANSACTION_isOaidTrackLimited: {
                    data.enforceInterface(DESCRIPTOR);
                    boolean result = this.isOaidTrackLimited();
                    reply.writeNoException();
                    reply.writeInt(result ? 1 : 0);
                    return true;
                }
                case TRANSACTION_getOAID: {
                    data.enforceInterface(DESCRIPTOR);
                    String result = this.getOaid();
                    reply.writeNoException();
                    reply.writeString(result);
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements OpenDeviceIdentifierService {

            private final IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            public String getOaid() throws RemoteException {
                Parcel var1 = Parcel.obtain();
                Parcel var2 = Parcel.obtain();

                String var3;
                try {
                    var1.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(1, var1, var2, 0);
                    var2.readException();
                    var3 = var2.readString();
                } finally {
                    var2.recycle();
                    var1.recycle();
                }

                return var3;
            }

            public boolean isOaidTrackLimited() throws RemoteException {
                Parcel var1 = Parcel.obtain();
                Parcel var2 = Parcel.obtain();

                boolean var3;
                try {
                    var1.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_isOaidTrackLimited, var1, var2, 0);
                    var2.readException();
                    var3 = 0 != var2.readInt();
                } finally {
                    var2.recycle();
                    var1.recycle();
                }

                return var3;
            }
        }
    }

    String getOaid() throws RemoteException;

    boolean isOaidTrackLimited() throws RemoteException;
}
