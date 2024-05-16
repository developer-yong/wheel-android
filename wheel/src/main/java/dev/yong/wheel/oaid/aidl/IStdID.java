package dev.yong.wheel.oaid.aidl;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IStdID extends IOpenID {

    abstract class Stub extends IOpenID.Stub {

        private static final String DESCRIPTOR = "com.oplus.stdid.IStdID";

        static final int TRANSACTION_getSerID = FIRST_CALL_TRANSACTION;

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IStdID asInterface(IBinder service) {
            if (service == null) {
                return null;
            }
            IInterface iInterface = service.queryLocalInterface(DESCRIPTOR);
            if (iInterface instanceof IStdID) {
                return (IStdID) iInterface;
            }
            return (IStdID) new Proxy(service);
        }


        private static class Proxy implements IStdID {

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
                    mRemote.transact(IStdID.Stub.TRANSACTION_getSerID, data, reply, 0);
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
}
