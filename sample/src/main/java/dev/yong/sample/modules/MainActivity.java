package dev.yong.sample.modules;

import android.Manifest;

import java.util.List;

import butterknife.OnClick;
import dev.yong.photo.PhotoSelector;
import dev.yong.sample.R;
import dev.yong.sample.modules.weather.WeatherActivity;
import dev.yong.wheel.base.BaseActivity;
import dev.yong.wheel.permission.Permission;
import dev.yong.wheel.utils.Logger;
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

    @OnClick(R.id.btn_start)
    public void onViewClicked() {
        Permission.with(this)
                .check(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(granted -> {
                    PhotoSelector.getInstance().select(this, selectPaths -> {
                        Logger.e(selectPaths.toArray(new String[]{}));
                    });
                });
//        startActivity(WeatherActivity.class);
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }


}
