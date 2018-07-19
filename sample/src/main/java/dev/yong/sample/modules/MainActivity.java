package dev.yong.sample.modules;

import android.Manifest;

import butterknife.OnClick;
import dev.yong.sample.R;
import dev.yong.sample.modules.login.LoginActivity;
import dev.yong.wheel.base.BaseActivity;
import dev.yong.wheel.permission.Permission;
import dev.yong.wheel.utils.SnackUtils;
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

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        Permission.with(this)
                .check(Manifest.permission.READ_PHONE_STATE)
                .request(granted -> {
                    if (granted) {
                        startActivity(LoginActivity.class);
//                            startActivity(WeatherActivity.class);
                    } else {
                        SnackUtils.show(getWindow().getDecorView(), "权限被拒绝");
                    }
                });
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    @Override
    protected boolean isInject() {
        return false;
    }
}
