package dev.yong.sample.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * 全局App完成注入超类
 * <p>
 * 该类继承AndroidInjector<DaggerApplication>为了减少AppComponent在Activity中的注入代码
 * <a>https://google.github.io/dagger//android.html</a>
 * </P>
 *
 * @author coderyong
 */
@Singleton
@Component(modules = {
        ActivityModule.class,
        FragmentModule.class,
        AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
