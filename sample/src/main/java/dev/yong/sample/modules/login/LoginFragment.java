package dev.yong.sample.modules.login;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import dev.yong.sample.R;
import dev.yong.sample.modules.weather.WeatherActivity;
import dev.yong.wheel.base.mvp.BaseMvpFragment;
import dev.yong.wheel.permission.Permission;
import dev.yong.wheel.utils.SnackUtils;
import dev.yong.wheel.utils.ToastUtils;
import dev.yong.wheel.view.ProgressDialog;

/**
 * @author coderyong
 */
public class LoginFragment extends BaseMvpFragment<LoginContract.View, LoginPresenter>
        implements LoginContract.View, LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.progress_login)
    ProgressBar mProgressLogin;
    @BindView(R.id.tv_username)
    AutoCompleteTextView mTvUsername;
    @BindView(R.id.tv_password)
    EditText mTvPassword;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    ProgressDialog mDialog;

//    @Inject
//    public LoginFragment() {
//    }

    @Override
    protected int createLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void init() {
        super.init();
        if (getActivity() != null) {
            Permission.with(getActivity())
                    .check(Manifest.permission.READ_CONTACTS)
                    .request(granted -> getLoaderManager().initLoader(0, null, LoginFragment.this));
        }

        mTvPassword.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });
    }

    @Override
    public void showErrorMessage(String message) {
        showProgress(false);
        SnackUtils.show(getView(), message);
    }

    @Override
    public void onSuccess() {
        showProgress(false);
        ToastUtils.show("登录成功");
        startActivity(WeatherActivity.class);
    }

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        attemptLogin();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mTvUsername.setError(null);
        mTvPassword.setError(null);

        // Store values at the time of the login attempt.
        String username = mTvUsername.getText().toString();
        String password = mTvPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mTvPassword.setError(getString(R.string.error_invalid_password));
            focusView = mTvPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mTvUsername.setError(getString(R.string.error_field_required));
            focusView = mTvUsername;
            cancel = true;
        } else if (!isEmailValid(username)) {
            mTvUsername.setError(getString(R.string.error_invalid_email));
            focusView = mTvUsername;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mPresenter.login(username, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(boolean show) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
        }
        if (show) {
            mDialog.show("登录中...");
        } else {
            mDialog.dismiss();
        }
    }

    private void addUserNamesToAutoComplete(List<String> names) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(mContext,
                        android.R.layout.simple_dropdown_item_1line, names);

        mTvUsername.setAdapter(adapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(mContext,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        List<String> names = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            names.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        names.add("foo@example.com");
        names.add("bar@example.com");
        addUserNamesToAutoComplete(names);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    @Override
    protected LoginContract.View provideVew() {
        return this;
    }

    @Override
    protected LoginPresenter providePresenter() {
        return new LoginPresenter();
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}
