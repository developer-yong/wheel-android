package dev.yong.wheel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.squareup.leakcanary.LeakCanary;

import org.simple.eventbus.EventBus;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.concurrent.Executors;

import dev.yong.wheel.utils.Logger;
import dev.yong.wheel.utils.ToastUtils;

import static dev.yong.wheel.BuildConfig.DEBUG;

/**
 * @author coderyong
 */

public class AppManager implements Thread.UncaughtExceptionHandler, Application.ActivityLifecycleCallbacks {

    @SuppressLint("StaticFieldLeak")
    private static AppManager sInstance = new AppManager();

    private AppManager() {
    }

    public static AppManager getInstance() {
        return sInstance;
    }

    private Application mApplication;
    private Class<?> mMainClazz;
    private boolean useEventBus;

    private Vector<Activity> mActivities = new Vector<>();

    public static void init(Application application) {
        init(application, null);
    }

    /**
     * 初始化App
     *
     * @param application Application
     * @param mainClazz   如有奔溃需要启动的Activity
     */
    public static void init(Application application, Class<?> mainClazz) {
        sInstance.mApplication = application;
        if (mainClazz != null) {
            sInstance.mMainClazz = mainClazz;
            //设置该CrashHandler为程序的默认处理器
            Thread.setDefaultUncaughtExceptionHandler(sInstance);
        }
        application.registerActivityLifecycleCallbacks(sInstance);
        //内存泄漏检测初始化
        LeakCanary.install(application);
    }

    public Application getApplication() {
        if (mApplication == null) {
            throw new IllegalStateException("Application not initialized, AppManager.init(Application) not implemented");
        }
        return mApplication;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        showToast();
        captureException(t, e);

        Intent intent = new Intent(mApplication, mMainClazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent restartIntent = PendingIntent.getActivity(
                mApplication, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //重启应用
        AlarmManager manager = (AlarmManager) mApplication.getSystemService(Context.ALARM_SERVICE);
        /* assert [boolean 表达式]
         * 如果[boolean表达式]为true，则程序继续执行。
         * 如果为false，则程序抛出AssertionError，并终止执行。
         */
        assert manager != null;
        manager.set(AlarmManager.RTC, System.currentTimeMillis(), restartIntent);
        //退出
        exit();
    }

    /**
     * 捕获异常信息
     *
     * @param t  奔溃线程
     * @param ex 奔溃异常
     */
    private void captureException(Thread t, Throwable ex) {

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        PackageInfo info = getPackageInfo();
        map.put("手机品牌", "" + Build.BRAND);
        map.put("手机型号", "" + Build.MODEL);
        map.put("SDK版本", "" + Build.VERSION.SDK_INT);
        map.put("versionName", info.versionName);
        map.put("versionCode", "" + info.versionCode);

        String msg = t.getName() + " Thread" + "异常崩溃\n设备信息：" + map.toString();
        if (DEBUG) {
            Logger.e(ex, t.getName());
        } else {
            Logger.file(mApplication, msg, ex);
        }
    }

    private void showToast() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                ToastUtils.show("很抱歉，程序出现异常，即将重启", Toast.LENGTH_LONG);
                Looper.loop();
            }
        });
        try {
            //Toast展示的时间
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Logger.e(e, "AppRestart");
        }
    }

    /**
     * 获取应用信息
     *
     * @return PackageInfo
     */
    public PackageInfo getPackageInfo() {
        try {
            String packageName = getApplication().getPackageName();
            return getApplication().getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isUseEventBus() {
        return useEventBus;
    }

    public void setUseEventBus(boolean useEventBus) {
        this.useEventBus = useEventBus;
    }

    /**
     * 获取栈顶的Activity
     *
     * @return Activity
     */
    public Activity getTopActivity() {
        if (mActivities.size() > 0) {
            return mActivities.get(mActivities.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * 获取上一个Activity
     *
     * @return Activity
     */
    public Activity getPreActivity() {
        if (mActivities.size() > 1) {
            return mActivities.get(mActivities.size() - 2);
        } else {
            return null;
        }
    }

    /**
     * 销毁所有Activity
     */
    public void clearAllActivity(){
        synchronized (AppManager.class) {
            Iterator<Activity> iterator = mActivities.iterator();
            while (iterator.hasNext()) {
                Activity a = iterator.next();
                iterator.remove();
                a.finish();
            }
        }
    }

    /**
     * 退出当前应用程序
     */
    public void exit() {
        EventBus.getDefault().clear();
        clearAllActivity();
        mActivities = null;
        mMainClazz = null;
        mApplication = null;
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        System.gc();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (mActivities.indexOf(activity) == -1) {
            mActivities.add(activity);
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivities.remove(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }
}
