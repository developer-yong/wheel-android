package dev.yong.sample.modules.login;

import dev.yong.sample.R;

import dev.yong.wheel.base.BaseFragment;

/**
 * @author coderyong
 */
public class RegisterFragment extends BaseFragment {

//    @Inject
//    public RegisterFragment() {
//    }

    @Override
    protected int createLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }
}
