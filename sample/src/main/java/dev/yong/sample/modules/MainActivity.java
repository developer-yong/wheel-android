package dev.yong.sample.modules;

import butterknife.OnClick;
import dev.yong.sample.R;
import dev.yong.sample.modules.weather.WeatherActivity;
import dev.yong.wheel.base.BaseActivity;
import dev.yong.wheel.utils.StatusBar;


/**
 * @author coderyong
 */
public class MainActivity extends BaseActivity {

    @Override
    protected int layoutId() {
        StatusBar.translucent(this, false);
        return R.layout.activity_main;
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    @OnClick(R.id.btn_start)
    public void onViewClicked() {
        startActivity(WeatherActivity.class);
    }
}
