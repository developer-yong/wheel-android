package dev.yong.wheel.http;

import android.support.annotation.NonNull;

/**
 * @author coderyong
 */
public interface ResponseVerify {

    void verify(@NonNull VerifyListener listener, int code, String message);

    interface VerifyListener {
        void onSuccess();

        void onFail(int code, String message);
    }
}
