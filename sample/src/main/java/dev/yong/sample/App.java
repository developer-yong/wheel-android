package dev.yong.sample;


import android.content.Context;
import android.support.multidex.MultiDex;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.Map;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dev.yong.sample.di.DaggerAppComponent;
import dev.yong.wheel.AppManager;
import dev.yong.wheel.http.Http;
import dev.yong.wheel.http.HttpInterceptor;
import dev.yong.wheel.http.HttpResponse;

/**
 * @author coderyong
 */
public class App extends DaggerApplication {

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
//        //设置网络响应验证
//        Http.getInstance().setHttpInterceptor(new HttpInterceptor() {
//            @Override
//            public void resetParameters(Map<String, String> parameters) {
//
//            }
//
//            @Override
//            public void onVerify(Map<String, String> parameters, String responseBody, Callback listener) {
//                Http.request("https://www.baidu.com/").get(new HttpResponse<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//                        listener.retry(parameters);
//                    }
//
//                    @Override
//                    public void onFail(Throwable t) {
//                        listener.response(responseBody);
//                    }
//                });
//            }
//        });
//        RetrofitHelper.getInstance().beforeInterceptor(new BeforeInterceptor() {
//
//            @NonNull
//            @Override
//            public Response onResponseBefore(Interceptor.Chain chain, Response response) {
//
//                try {
//                    return chain.proceed(chain.request());
//                } catch (IOException e) {
//                    return response;
//                }
//            }
//
//            @NonNull
//            @Override
//            public Request onRequestBefore(Request request) {
//
//                return request;
//            }
//        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
