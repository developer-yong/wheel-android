package dev.yong.swipeback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Vector;


/**
 * @author coderyong
 */
public class ActivityStack implements Application.ActivityLifecycleCallbacks {

    private static ActivityStack sInstance = new ActivityStack();

    private Vector<Activity> mActivities = new Vector<>();

    public static ActivityStack getInstance() {
        return sInstance;
    }

    public ActivityStack() {
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
