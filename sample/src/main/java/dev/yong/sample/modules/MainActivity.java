package dev.yong.sample.modules;

import android.Manifest;
import butterknife.OnClick;
import dev.yong.photo.PhotoSelector;
import dev.yong.sample.R;
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

    @Override
    protected void init() {
        super.init();
        Logger.e(System.getProperty("line.separator"));
    }

    @OnClick(R.id.btn_start)
    public void onViewClicked() {

        Permission.with(this)
                .check(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(granted -> PhotoSelector.getInstance()
                        .enableCamera(true)
                        .select(this, selectPaths -> {
                            for (String path : selectPaths) {
                                Logger.e(path);
                            }
                        }));
//        startActivity(WeatherActivity.class);
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }


}
