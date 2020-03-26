package dev.yong.wheel.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev.yong.wheel.R;
import dev.yong.wheel.utils.DensityUtils;

/**
 * @author CoderYong
 */
public class BottomMenuDialog extends Dialog {

    private LinearLayout mLinearLayout;

    public BottomMenuDialog(Context context) {
        super(context, R.style.BottomMenuDialog);
        mLinearLayout = (LinearLayout) View.inflate(getContext(), R.layout.dialog_bottom_menu, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLinearLayout);
        findViewById(R.id.btn_cancel).setOnClickListener(v -> cancel());
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
            window.setWindowAnimations(R.style.BottomMenuAnimation);
        }
    }

    public void setMenu(OnMenuItemClickListener listener, String... menu) {
        if (mLinearLayout != null && listener != null) {
            for (int i = menu.length - 1; i >= 0; i--) {
                TextView btnMenu = new TextView(getContext());
                btnMenu.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                btnMenu.setGravity(Gravity.CENTER);
                int padding = DensityUtils.dip2px(15);
                btnMenu.setPadding(padding, padding, padding, padding);
                btnMenu.setText(menu[i]);
                btnMenu.setBackgroundResource(R.drawable.bg_item_click);
                btnMenu.setTextColor(Color.BLACK);
                btnMenu.setTextSize(16);
                mLinearLayout.addView(btnMenu, 0);
                int position = i;
                btnMenu.setOnClickListener(v -> {
                    listener.onItemClick(position);
                    cancel();
                });
            }
        }
    }

    public interface OnMenuItemClickListener {
        void onItemClick(int position);
    }
}
