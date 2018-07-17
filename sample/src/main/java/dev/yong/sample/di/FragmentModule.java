package dev.yong.sample.di;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dev.yong.sample.modules.login.LoginFragment;
import dev.yong.sample.modules.login.RegisterFragment;
import dev.yong.sample.modules.weather.WeatherFragment;
import dev.yong.wheel.di.FragmentScoped;

/**
 * 所有Activity依赖提供类
 * <a>https://google.github.io/dagger//android.html</a>
 *
 * @author coderyong
 */
@Module
interface FragmentModule {

    @FragmentScoped
    @ContributesAndroidInjector
    LoginFragment loginFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    RegisterFragment registerFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    WeatherFragment weatherFragment();
}
