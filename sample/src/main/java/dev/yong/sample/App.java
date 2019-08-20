package dev.yong.sample;


import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.io.File;
import java.util.concurrent.TimeUnit;

import dev.yong.wheel.AppManager;
import dev.yong.wheel.http.HttpHelper;
import dev.yong.wheel.http.interceptor.LoggerInterceptor;
import okhttp3.Cache;

/**
 * @author coderyong
 */
public class App extends Application {

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            //指定为经典Header
            return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) ->
                new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppManager.init(this);
//        HttpHelper.okHttp()
//                .addInterceptor(new LoggerInterceptor())
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
