package dev.yong.wheel.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;


public class Keyboard implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String INPUT_HEIGHT = "soft_input_height";

    private Activity mActivity;
    private View mRootView;
    private InputMethodManager mInputManager;
    private SharedPreferences mPreferences;

    private EditText mEditText;
    private View mContainerView;
    private View mSwitchLayout;

    private List<SoftKeyboardStateListener> mListeners = new LinkedList<>();
    private boolean isSoftKeyboardOpened = false;

    private Keyboard() {
    }


    /**
     * 初始化软件盘高度
     *
     * @param rootView 根布局视图
     */
    public static void initKeyboardHeight(View rootView) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int keyboardHeight = rootView.getHeight() - r.bottom;
            //减去虚拟按键高度
            keyboardHeight = keyboardHeight - getVirtualKeyHeight(rootView.getContext());
            if (keyboardHeight > 500) {
                SharedPreferences preferences = rootView.getContext()
                        .getSharedPreferences("Keyboard", Context.MODE_PRIVATE);
                preferences.edit().putInt(INPUT_HEIGHT, keyboardHeight).apply();
            }
        });
    }

    /**
     * 外部静态调用
     *
     * @param activity 作用Activity
     */
    public static Keyboard with(Activity activity) {
        Keyboard keyboard = new Keyboard();
        keyboard.mActivity = activity;
        keyboard.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.mPreferences = activity.getSharedPreferences("Keyboard", Context.MODE_PRIVATE);
        keyboard.mRootView = activity.getWindow().getDecorView().getRootView();
        keyboard.mRootView.getViewTreeObserver().addOnGlobalLayoutListener(keyboard);
        return keyboard;
    }

    /**
     * 绑定内容view，此view用于固定bar的高度，防止跳闪
     *
     * @param containerView 内容布局
     */
    public Keyboard bindContainer(View containerView) {
        mContainerView = containerView;
        return this;
    }

    /**
     * 绑定编辑框
     *
     * @param editText 文本框
     */
    @SuppressLint("ClickableViewAccessibility")
    public Keyboard bindEditText(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && mSwitchLayout.isShown()) {
                //显示软件盘时，锁定内容高度，防止跳闪。
                lockContentHeight();
                //软件盘显示后，释放内容高度
                unlockContentHeightDelayed();
            }
            return false;
        });
        return this;
    }

    /**
     * 绑定切换布局
     *
     * @param switchLayout 切换布局
     * @param switchButton 切换控制按钮
     */
    public Keyboard bindSwitchLayout(View switchLayout, View switchButton) {
        mSwitchLayout = switchLayout;
        if (switchButton != null) {
            switchButton.setOnClickListener(v -> {
                if (mSwitchLayout.isShown()) {
                    if (isSoftInputShown()) {
                        hideSoftInput();
                    } else {
                        showSoftInput();
                    }
                } else {
                    showSwitchLayout();
                }
            });
        }
        return this;
    }

    public Keyboard build() {
        //设置软件盘的模式：SOFT_INPUT_ADJUST_RESIZE  这个属性表示Activity的主窗口总是会被调整大小，从而保证软键盘显示空间。
        //从而方便我们计算软件盘的高度
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //隐藏软件盘
        hideSoftInput();
        return this;
    }

    /**
     * 点击返回键时先隐藏布局
     */
    public boolean interceptBackPress() {
        if (mSwitchLayout.isShown()) {
            hideSwitchLayout();
            return true;
        }
        return false;
    }

    public void showSwitchLayout() {
        if (isSoftInputShown()) {
            lockContentHeight();
        }
        hideSoftInput();
        unlockContentHeightDelayed();
        mSwitchLayout.getLayoutParams().height = getKeyBoardHeight();
        mSwitchLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏布局
     */
    public void hideSwitchLayout() {
        if (mSwitchLayout.isShown()) {
            mSwitchLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void showSoftInput() {
        if (mSwitchLayout != null && mSwitchLayout.isShown()) {
            lockContentHeight();
        }
        mEditText.requestFocus();
        mEditText.post(() -> mInputManager.showSoftInput(mEditText, 0));
        unlockContentHeightDelayed();
    }

    /**
     * 隐藏软件盘
     */
    private void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 是否显示软件盘
     *
     * @return 是否显示软件盘
     */
    private boolean isSoftInputShown() {
        return isSoftKeyboardOpened;
    }

    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        mRootView.getWindowVisibleDisplayFrame(r);
        //获取键盘高度
        int softInputHeight = mRootView.getHeight() - r.bottom;
        //减去虚拟按键高度
        softInputHeight = softInputHeight - getVirtualKeyHeight(mActivity);
        //存一份到本地
        if (Math.abs(softInputHeight - getKeyBoardHeight()) < 200) {
            mPreferences.edit().putInt(INPUT_HEIGHT, softInputHeight).apply();
        }
        if (!isSoftKeyboardOpened && softInputHeight > 500) {
            isSoftKeyboardOpened = true;
            for (SoftKeyboardStateListener listener : mListeners) {
                if (listener != null) {
                    listener.onSoftKeyboardOpened(softInputHeight);
                }
            }
        } else if (isSoftKeyboardOpened && softInputHeight < 500) {
            isSoftKeyboardOpened = false;
            for (SoftKeyboardStateListener listener : mListeners) {
                if (listener != null) {
                    listener.onSoftKeyboardClosed();
                }
            }
        }
    }

    /**
     * 获取软键盘高度，由于第一次直接弹出时会出现小问题，787是一个均值，作为临时解决方案
     *
     * @return 键盘高度
     */
    public int getKeyBoardHeight() {
        Log.e("Keyboard", "getKeyBoardHeight: " + mPreferences.getInt(INPUT_HEIGHT, 787));
        return mPreferences.getInt(INPUT_HEIGHT, 787);
    }


    /**
     * 锁定内容高度，防止跳闪
     */
    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContainerView.getLayoutParams();
        params.height = mContainerView.getHeight();
        params.weight = 0.0F;
    }

    /**
     * 释放被锁定的内容高度
     */
    private void unlockContentHeightDelayed() {
        mEditText.postDelayed(() -> ((LinearLayout.LayoutParams)
                mContainerView.getLayoutParams()).weight = 1.0F, 200L);
    }

    /**
     * 添加键盘监听事件
     *
     * @param listener 键盘监听
     */
    public void addSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        mListeners.add(listener);
    }

    public interface SoftKeyboardStateListener {
        /**
         * 软键盘打开
         *
         * @param keyboardHeight 键盘高度
         */
        void onSoftKeyboardOpened(int keyboardHeight);

        /**
         * 软键盘关闭
         */
        void onSoftKeyboardClosed();
    }


    /**
     * 获取 虚拟按键的高度
     *
     * @param context 上下文对象
     * @return 虚拟按键的高度
     */
    public static int getVirtualKeyHeight(Context context) {
        if (checkVirtualKeyShow(context)) {
            int totalHeight = getDpi(context);
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return totalHeight - dm.heightPixels;
        } else {
            return 0;
        }
    }

    /**
     * 获取屏幕原始尺寸高度，包括虚拟功能键高度
     *
     * @param context 上下文对象
     * @return 屏幕原始尺寸
     */
    private static int getDpi(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 判断虚拟导航栏是否显示
     *
     * @param context 上下文对象
     * @return true(显示虚拟导航栏)，false(不显示或不支持虚拟导航栏)
     */
    private static boolean checkVirtualKeyShow(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            //判断是否隐藏了底部虚拟导航
            int navigationBarIsMin = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                navigationBarIsMin = Settings.System.getInt(context.getContentResolver(),
                        "navigationbar_is_min", 0);
            } else {
                navigationBarIsMin = Settings.Global.getInt(context.getContentResolver(),
                        "navigationbar_is_min", 0);
            }
            if ("1".equals(navBarOverride) || 1 == navigationBarIsMin) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }

    public static void hide(View v) {
        Context context = v.getContext();
        if (context != null) {
            //隐藏键盘
            InputMethodManager manager = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    public static void show(View v) {
        Context context = v.getContext();
        if (context != null) {
            //显示键盘
            InputMethodManager manager = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.showSoftInput(v, 1);
            }
        }
    }
}
