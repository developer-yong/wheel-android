package dev.yong.sample.modules.login;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

/**
 * @author coderyong
 */
public class LoginPresenter implements LoginContract.Presenter {

    /**
     * A dummy authentication store containing known user names and passwords.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private LoginTask mAuthTask = null;

    private LoginContract.View mView;

    LoginPresenter() {
    }

    @Override
    public void login(String username, String password) {
        if (mView != null) {
            mAuthTask = new LoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    @Override
    public void takeView(LoginContract.View view) {
        mView = view;
    }

    @Override
    public void dropView() {
        mView = null;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @SuppressLint("StaticFieldLeak")
    public class LoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserName;
        private final String mPassword;

        LoginTask(String username, String password) {
            mUserName = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mUserName)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mAuthTask = null;

            if (success) {
                mView.onSuccess();
            } else {
                mView.showErrorMessage("登录失败，用户名或密码错误！");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
