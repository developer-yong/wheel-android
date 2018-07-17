package dev.yong.sample.modules.login;


import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dev.yong.wheel.di.FragmentScoped;

/**
 * @author coderyong
 */
@Module
public class LoginModule {

    @Provides
    LoginFragment provideLoginFragment(){
        return new LoginFragment();
    }

    @Provides
    RegisterFragment provideRegisterFragment(){
        return new RegisterFragment();
    }
}
