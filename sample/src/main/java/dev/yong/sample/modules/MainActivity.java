package dev.yong.sample.modules;

import android.Manifest;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

import butterknife.OnClick;
import dev.yong.photo.PhotoSelector;
import dev.yong.sample.R;
import dev.yong.wheel.base.BaseActivity;
import dev.yong.wheel.permission.Permission;
import dev.yong.wheel.utils.SnackUtils;
import dev.yong.wheel.utils.StatusBar;
import dev.yong.wheel.utils.ToastUtils;


/**
 * @author coderyong
 */
public class MainActivity extends BaseActivity {

    @Override
    protected int layoutId() {
        StatusBar.translucent(this, false);
        return R.layout.activity_main;
    }

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
//        PhotoSelector.with(this).setEnableCamera(true);

        Permission.with(this)
                .check(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(granted -> {
                    if (granted) {
//                        startActivity(LoginActivity.class);
//                            startActivity(WeatherActivity.class);
//                        startActivity(SelectorActivity.class);
                        PhotoSelector.getInstance()
                                .configCameraEnable(true)
                                .select(this, selectFiles -> ToastUtils.show(selectFiles.toString()));
                    } else {
                        SnackUtils.show(getWindow().getDecorView(), "权限被拒绝");
                    }
                });
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

}
