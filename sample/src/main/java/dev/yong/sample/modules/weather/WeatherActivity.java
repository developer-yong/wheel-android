package dev.yong.sample.modules.weather;

import javax.inject.Inject;

import dev.yong.sample.R;
import dev.yong.wheel.base.BaseActivity;

/**
 * @author coderyong
 */
public class WeatherActivity extends BaseActivity {

    @Inject
    WeatherFragment mWeatherFragment;

    @Override
    protected int layoutId() {
        return R.layout.activity_weather;
    }

    @Override
    protected void init() {
        super.init();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_content, mWeatherFragment).commit();
    }

    @Override
    public boolean hasAlpha() {
        return true;
    }
}
