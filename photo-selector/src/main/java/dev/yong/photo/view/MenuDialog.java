package dev.yong.photo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import dev.yong.photo.R;


/**
 * @author CoderYong
 */
public class MenuDialog extends Dialog {

    private OnMenuClickListener mOnMenuClickListener;

    public MenuDialog(Context context) {
        super(context, R.style.MenuDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(View.inflate(getContext(), R.layout.dialog_menu, null));
        findViewById(R.id.btn_cancel).setOnClickListener(v -> cancel());
        findViewById(R.id.btn_take_photo).setOnClickListener(v -> {
            if (mOnMenuClickListener != null) {
                mOnMenuClickListener.onTakePhotoClick();
            }
            cancel();
        });
        findViewById(R.id.btn_record_video).setOnClickListener(v -> {
            if (mOnMenuClickListener != null) {
                mOnMenuClickListener.onRecordVideoClick();
            }
            cancel();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setWindowAnimations(R.style.MenuAnimation);
        }
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        mOnMenuClickListener = listener;
    }

    public interface OnMenuClickListener {

        void onTakePhotoClick();

        void onRecordVideoClick();
    }
}
