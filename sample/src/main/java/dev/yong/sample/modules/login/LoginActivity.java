package dev.yong.sample.modules.login;

import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import dev.yong.sample.R;
import dev.yong.wheel.base.BaseActivity;

/**
 * @author coderyong
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected int layoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_content, new LoginFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_register:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.layout_content, new RegisterFragment()).commit();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean prevViewScrollable() {
        return false;
    }
}
