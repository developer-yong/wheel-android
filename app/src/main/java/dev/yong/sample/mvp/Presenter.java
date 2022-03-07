package dev.yong.sample.mvp;

import org.jetbrains.annotations.NotNull;

import dev.yong.wheel.http.Callback;
import dev.yong.wheel.http.OkHttpHelper;

/**
 * @author wuyongzhi (rwuyongzhi_v@didiglobal.com)
 * @date 2021/8/4
 */
public class Presenter {

    public Presenter() {
        OkHttpHelper.get("")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(String s) {

                    }

                    @Override
                    public void onFailure(@NotNull Throwable t) {

                    }
                });

    }
}
