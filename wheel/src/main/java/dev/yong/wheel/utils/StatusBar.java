package dev.yong.wheel.utils;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author coderyong
 */
public class StatusBar {

    /**
     * 沉浸状态栏
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    public static void setColor(Activity activity, @ColorInt int color) {
        if (color != -1) {
            // 绘制一个和状态栏一样高的矩形
            View statusView = new View(activity);
            statusView.setBackgroundColor(color);
            setView(activity, statusView);
        }
    }

    /**
     * 设置一个假的状态栏
     *
     * @param activity   需要设置的activity
     * @param statusView 状态栏View
     */
    public static void setView(Activity activity, View statusView) {
        //去除 ActionBar 阴影
        if (hasActionBar(activity)) {
            if (activity instanceof AppCompatActivity) {
                ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setElevation(0);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    android.app.ActionBar actionBar = activity.getActionBar();
                    if (actionBar != null) {
                        actionBar.setElevation(0);
                    }
                }
            }
        }
        translucent(activity, false);
        ViewGroup contentLayout = activity.findViewById(android.R.id.content);
        // 设置Activity layout的fitsSystemWindows
        View child = contentLayout.getChildAt(0);
        if (child != null) {
            child.setFitsSystemWindows(true);
        }

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getHeight(activity));

        contentLayout.addView(statusView, params);
    }

    /**
     * 设置状态栏透明
     *
     * @param activity 需要设置的activity
     */
    public static void translucent(Activity activity, boolean addBarHeight) {
        translucent(activity, addBarHeight, false);
    }

    /**
     * 设置状态栏透明
     *
     * @param activity 需要设置的activity
     */
    public static void translucent(Activity activity, boolean addBarHeight, boolean isLight) {
        Window window = activity.getWindow();
        //4.4 全透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //5.0 全透明实现
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | (isLight && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0);
            window.getDecorView().setSystemUiVisibility(visibility);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        if (addBarHeight) {
            int barHeight = getHeight(activity);
            if (hasActionBar(activity)) {
                barHeight += getActionBarHeight(activity);
            }
            ViewGroup rootView = window.getDecorView().findViewById(android.R.id.content);
            rootView.setPadding(0, barHeight, 0, 0);
        }
    }

    private static boolean hasActionBar(Activity activity) {
        if (activity instanceof AppCompatActivity) {
            return ((AppCompatActivity) activity).getSupportActionBar() != null;
        } else {
            return activity.getActionBar() != null;
        }
    }

    private static int getActionBarHeight(Activity activity) {
        TypedArray attrs = activity.obtainStyledAttributes(new int[]{
                android.R.attr.actionBarSize
        });
        attrs.recycle();
        return (int) attrs.getDimension(0, 0);
    }

    /**
     * 获取状态栏高度
     *
     * @param activity 需要设置的activity
     */
    public static int getHeight(Activity activity) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return activity.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 隐藏状态栏
     *
     * @param activity 需要设置的activity
     */
    public static void hide(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
