package dev.yong.sample.modules.login;

import dev.yong.wheel.base.mvp.IPresenter;

/**
 * @author coderyong
 */
public interface LoginContract {

    interface View {

        /**
         * 显示登录错误信息
         *
         * @param message 服务器返回信息
         */
        void showErrorMessage(String message);

        /**
         * 登录成功
         */
        void onSuccess();
    }

    interface Presenter extends IPresenter<View> {

        /**
         * 登录
         *
         * @param username 用户名
         * @param password 密码
         */
        void login(String username, String password);
    }
}
