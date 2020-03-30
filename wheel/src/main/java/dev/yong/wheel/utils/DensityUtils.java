package dev.yong.wheel.utils;


import dev.yong.wheel.AppManager;

/**
 * @author CoderYong
 */
public final class DensityUtils {

    private static float density = -1F;
    private static int widthPixels = -1;
    private static int heightPixels = -1;

    private DensityUtils() {
        throw new UnsupportedOperationException("Cannot be created");
    }

    public static float getDensity() {
        if (density <= 0F) {
            density = AppManager.getInstance().getApplication()
                    .getApplicationContext().getResources().getDisplayMetrics().density;
        }
        return density;
    }

    public static int dip2px(float dpValue) {
        return (int) (dpValue * getDensity() + 0.5F);
    }

    public static int px2dip(float pxValue) {
        return (int) (pxValue / getDensity() + 0.5F);
    }

    public static int getScreenWidth() {
        if (widthPixels <= 0) {
            widthPixels = AppManager.getInstance().getApplication()
                    .getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        }
        return widthPixels;
    }


    public static int getScreenHeight() {
        if (heightPixels <= 0) {
            heightPixels = AppManager.getInstance().getApplication()
                    .getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        }
        return heightPixels;
    }
}