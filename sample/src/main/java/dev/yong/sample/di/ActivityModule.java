package dev.yong.sample.di;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dev.yong.sample.modules.login.LoginActivity;
import dev.yong.sample.modules.login.LoginModule;
import dev.yong.sample.modules.weather.WeatherActivity;
import dev.yong.wheel.di.ActivityScoped;

/**
 * 所有Activity依赖提供类
 * <a>https://google.github.io/dagger//android.html</a>
 *
 * @author coderyong
 */
@Module
interface ActivityModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = LoginModule.class)
    LoginActivity loginActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    WeatherActivity weatherActivity();
}
