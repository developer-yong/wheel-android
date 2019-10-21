package dev.yong.wheel.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Objects;

import dev.yong.wheel.R;

/**
 * @author CoderYong
 */
public class ProgressDialog extends Dialog {

    private TextView mTvMessage;
    private View mView;

    public ProgressDialog(Context context) {
        this(context, R.style.ProgressDialog);
    }

    public ProgressDialog(Context context, int theme) {
        super(context, theme);
        mView = View.inflate(context, R.layout.dialog_progress, null);
        ProgressBar progressBar = (ProgressBar) mView.findViewById(R.id.progress);
        progressBar.setIndeterminate(true);
        mTvMessage = (TextView) mView.findViewById(R.id.tv_progress_content);

        WindowManager.LayoutParams params = Objects.requireNonNull(getWindow()).getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);
        setCancelable(false);
    }

    public void show(int resId) {
        show(getContext().getString(resId));
    }

    public void show(CharSequence message) {
        this.show();
        mTvMessage.setText(message);
    }
}
