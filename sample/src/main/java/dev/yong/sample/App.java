package dev.yong.sample;


import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dev.yong.sample.di.DaggerAppComponent;
import dev.yong.wheel.AppManager;

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
//        Http.getInstance().setResponseVerify(new ResponseVerify() {
//
//            @Override
//            public void verify(@NonNull VerifyListener listener, int code, String message) {
//                Preferences.getInstance(App.this).putString("login", "admin");
//                if (!TextUtils.isEmpty(Preferences.getInstance(App.this).getString("login"))) {
//                    listener.responseHandle();
//                } else {
//                    listener.onFail(code, message);
//                }
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
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
