package dev.yong.sample.mvp;

import androidx.annotation.NonNull;

import dev.yong.wheel.http.Callback;
import dev.yong.wheel.http.OkHttpHelper;
import okhttp3.Call;

/**
 * @author wuyongzhi (rwuyongzhi_v@didiglobal.com)
 * @date 2021/8/4
 */
public class Presenter {

    public Presenter() {
        OkHttpHelper.get("")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull String s) {

                    }

                    @Override
                    public void onFailed(@NonNull Call call, @NonNull Throwable t) {

                    }
                });

    }
}
