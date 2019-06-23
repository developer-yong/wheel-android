package dev.yong.sample.modules.weather;

import android.support.v4.content.ContextCompat;

import dev.yong.sample.R;
import dev.yong.wheel.base.BaseActivity;

/**
 * @author coderyong
 */
public class WeatherActivity extends BaseActivity {

    @Override
    protected int layoutId() {
        return R.layout.activity_weather;
    }

    @Override
    protected void init() {
        super.init();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_content, new WeatherFragment()).commit();
    }

    @Override
    public boolean hasAlpha() {
        return true;
    }

    @Override
    public int statusBarColor() {
        return ContextCompat.getColor(this, R.color.colorPrimary);
    }
}
