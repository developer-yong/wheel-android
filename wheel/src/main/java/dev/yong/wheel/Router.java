package dev.yong.wheel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author coderyong
 */
@SuppressWarnings("unused")
public class Router {

    /**
     * 初始化Router
     *
     * @param application Application
     * @param interceptor Router拦截器
     *                    {@link RouterInterceptor#intercept(String)}
     */
    public static void init(@NonNull Application application, RouterInterceptor interceptor) {
        application.registerActivityLifecycleCallbacks(
                new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(
                            @NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                    }

                    @Override
                    public void onActivityStarted(@NonNull Activity activity) {
                    }

                    @Override
                    public void onActivityResumed(@NonNull Activity activity) {
                        RouterHolder.INSTANCE.mRunningActivity = activity;
                    }

                    @Override
                    public void onActivityPaused(@NonNull Activity activity) {
                    }

                    @Override
                    public void onActivityStopped(@NonNull Activity activity) {
                    }

                    @Override
                    public void onActivitySaveInstanceState(
                            @NonNull Activity activity, @NonNull Bundle outState) {
                    }

                    @Override
                    public void onActivityDestroyed(@NonNull Activity activity) {
                        if (RouterHolder.INSTANCE.mRunningActivity == activity) {
                            RouterHolder.INSTANCE.mRunningActivity = null;
                        }
                    }
                });
        RouterHolder.INSTANCE.mApp = application;
        RouterHolder.INSTANCE.mInterceptor = interceptor;
    }

    /**
     * 注册Activity页面
     *
     * @param pagePath 页面路径
     * @param aClass   Activity页面
     */
    public static void registerActivityPage(String pagePath, Class<? extends Activity> aClass) {
        RouterHolder.INSTANCE.mPages.put(pagePath, aClass);
    }

    /**
     * 注册Fragment页面
     *
     * @param pagePath 页面路径
     * @param fClass   Fragment页面
     */
    public static void registerFragmentPage(String pagePath, Class<? extends Fragment> fClass) {
        RouterHolder.INSTANCE.mPages.put(pagePath, fClass);
    }

    /**
     * 注册Fragment过渡动画
     * {@link FragmentTransaction#setCustomAnimations(int, int, int, int)}
     *
     * @param enter    入栈时进入动画
     * @param exit     入栈时退出动画
     * @param popEnter 出栈时进入动画
     * @param popExit  出栈时退出动画
     */
    public static void registerFragmentTransition(
            @AnimatorRes @AnimRes int enter,
            @AnimatorRes @AnimRes int exit,
            @AnimatorRes @AnimRes int popEnter,
            @AnimatorRes @AnimRes int popExit) {
        RouterHolder.INSTANCE.mEnter = enter;
        RouterHolder.INSTANCE.mExit = exit;
        RouterHolder.INSTANCE.mPopEnter = popEnter;
        RouterHolder.INSTANCE.mPopExit = popExit;
    }

    /**
     * 页面是否未注册
     *
     * @param pagePath 页面路径
     * @return true未注册，false已注册
     */
    public static boolean isUnregister(String pagePath) {
        return !RouterHolder.INSTANCE.mPages.containsKey(pagePath);
    }

    /**
     * 页面是否是Activity
     *
     * @param pagePath 页面路径
     * @return true为Activity，false为Fragment或者未注册
     */
    public static boolean isActivity(String pagePath) {
        Class<?> clazz = RouterHolder.INSTANCE.mPages.get(pagePath);
        return clazz != null && Activity.class.isAssignableFrom(clazz);
    }

    /**
     * 页面是否是Fragment
     *
     * @param pagePath 页面路径
     * @return true为Fragment，false为Activity或者未注册
     */
    public static boolean isFragment(String pagePath) {
        Class<?> clazz = RouterHolder.INSTANCE.mPages.get(pagePath);
        return clazz != null && Fragment.class.isAssignableFrom(clazz);
    }

    public static ActivityIntentBuilder with() {
        if (RouterHolder.INSTANCE.mApp == null) {
            throw new IllegalStateException("You must use Router.init() for initialization");
        }
        Context withContext = RouterHolder.INSTANCE.mApp;
        if (RouterHolder.INSTANCE.mRunningActivity != null) {
            withContext = RouterHolder.INSTANCE.mRunningActivity;
        }
        return new ActivityIntentBuilder(withContext);
    }

    public static ActivityIntentBuilder with(@NonNull Context context) {
        return new ActivityIntentBuilder(context);
    }

    public static FragmentTransactionBuilder with(
            @NonNull FragmentActivity activity, @IdRes int containerViewId) {
        return new FragmentTransactionBuilder(activity, containerViewId);
    }

    /**
     * 结束当前Fragment
     *
     * @param fragment 当前显示的Fragment
     * @param enter    进入动画
     * @param exit     退出动画
     */
    public static void finish(@NonNull Fragment fragment,
                              @AnimatorRes @AnimRes int enter,
                              @AnimatorRes @AnimRes int exit) {
        FragmentActivity activity = fragment.requireActivity();
        FragmentManager manager = activity.getSupportFragmentManager();
        if (manager.getFragments().size() < 2) {
            activity.onBackPressed();
        } else {
            manager.beginTransaction()
                    .setCustomAnimations(enter, exit)
                    .remove(fragment)
                    .commit();
            manager.popBackStackImmediate();
        }
    }

    /**
     * 结束当前Fragment
     *
     * @param fragment 当前显示的Fragment
     */
    public static void finish(@NonNull Fragment fragment) {
        finish(fragment, RouterHolder.INSTANCE.mPopEnter, RouterHolder.INSTANCE.mPopExit);
    }

    /**
     * 结束当前Activity
     *
     * @param activity 当前显示的Activity
     * @param enter    进入动画
     * @param exit     退出动画
     */
    public static void finish(@NonNull FragmentActivity activity,
                              @AnimatorRes @AnimRes int enter,
                              @AnimatorRes @AnimRes int exit) {
        FragmentManager manager = activity.getSupportFragmentManager();
        //获取当前Activity内所有的Fragment
        List<Fragment> fragments = manager.getFragments();
        //获取当前显示的Fragment
        Fragment currFragment = null;
        for (Fragment fragment : fragments) {
            if (fragment.isVisible()) {
                currFragment = fragment;
            }
        }
        if (currFragment == null || fragments.size() < 2) {
            activity.onBackPressed();
        } else {
            manager.beginTransaction()
                    .setCustomAnimations(enter, exit)
                    .remove(currFragment)
                    .commit();
            manager.popBackStackImmediate();
        }
    }

    /**
     * 结束当前Activity
     *
     * @param activity 当前显示的Activity
     */
    public static void finish(@NonNull FragmentActivity activity) {
        finish(activity, RouterHolder.INSTANCE.mPopEnter, RouterHolder.INSTANCE.mPopExit);
    }

    /**
     * 解析Uri中的参数
     * <p>
     * 如果未解析到参数返回空的Bundle
     * </P>
     *
     * @param uri Uri
     * @return 参数集合Bundle
     */
    @NonNull
    public static Bundle parseUri(@NonNull Uri uri) {
        Bundle b = new Bundle();
        Set<String> keys = uri.getQueryParameterNames();
        for (String key : keys) {
            b.putString(key, uri.getQueryParameter(key));
        }
        return b;
    }

    /**
     * Router页面集合
     */
    private final HashMap<String, Class<?>> mPages = new HashMap<>();

    /**
     * Fragment过渡动画
     */
    private int mEnter = R.anim.slide_in_right;
    private int mExit = R.anim.slide_out_left;
    private int mPopEnter = R.anim.slide_in_left;
    private int mPopExit = R.anim.slide_out_right;

    /**
     * Router动作监听
     */
    private RouterInterceptor mInterceptor;

    private Application mApp;
    /**
     * 当前正在运行的Activity
     */
    private Activity mRunningActivity;

    private Router() {
    }

    private static class RouterHolder {
        @SuppressLint("StaticFieldLeak")
        private final static Router INSTANCE = new Router();
    }

    public static class ActivityIntentBuilder extends Intent {

        private final Router mRouter = RouterHolder.INSTANCE;
        private final Context mContext;

        public ActivityIntentBuilder(@NonNull Context context) {
            mContext = context;
        }

        /**
         * 启动Activity页面
         *
         * <p>
         * 如果需要处理特殊路由{@link Router#init(Application, RouterInterceptor)},
         * 当未初始化{@link RouterInterceptor}且页面未被注册{@link Router#registerActivityPage(String, Class)}时,
         * 使用此方法不会抛出异常，同时也不会有新页面启动；
         * 当启动页面已注册且被{@link RouterInterceptor#intercept(String)}
         * 拦截处理返回false后页面仍会正常启动，反之只执行拦截处理内容
         * </P>
         *
         * @param pagePath 页面路径
         */
        public void start(String pagePath) {
            if (mRouter.mInterceptor != null) {
                if (mRouter.mInterceptor.intercept(pagePath)) {
                    return;
                }
            }
            if (isActivity(pagePath)) {
                setClass(mContext, Objects.requireNonNull(mRouter.mPages.get(pagePath)));
                //添加参数
                putExtras(Router.parseUri(Uri.parse(pagePath)));
                mContext.startActivity(this);
            }
        }

        /**
         * 启动Activity
         *
         * @param aClass Activity.class
         */
        public void start(Class<?> aClass) {
            setClass(mContext, aClass);
            mContext.startActivity(this);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setAction(@Nullable String action) {
            return (ActivityIntentBuilder) super.setAction(action);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setData(@Nullable Uri data) {
            return (ActivityIntentBuilder) super.setData(data);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setDataAndNormalize(@NonNull Uri data) {
            return (ActivityIntentBuilder) super.setDataAndNormalize(data);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setType(@Nullable String type) {
            return (ActivityIntentBuilder) super.setType(type);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setTypeAndNormalize(@Nullable String type) {
            return (ActivityIntentBuilder) super.setTypeAndNormalize(type);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setDataAndType(@Nullable Uri data, @Nullable String type) {
            return (ActivityIntentBuilder) super.setDataAndType(data, type);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setDataAndTypeAndNormalize(
                @NonNull Uri data, @Nullable String type) {
            return (ActivityIntentBuilder) super.setDataAndTypeAndNormalize(data, type);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder addCategory(String category) {
            return (ActivityIntentBuilder) super.addCategory(category);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, boolean value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, byte value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, char value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, short value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, int value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, long value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, float value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, double value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, String value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, CharSequence value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, Parcelable value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, Parcelable[] value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putParcelableArrayListExtra(
                String name, ArrayList<? extends Parcelable> value) {
            return (ActivityIntentBuilder) super.putParcelableArrayListExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
            return (ActivityIntentBuilder) super.putIntegerArrayListExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putStringArrayListExtra(String name, ArrayList<String> value) {
            return (ActivityIntentBuilder) super.putStringArrayListExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putCharSequenceArrayListExtra(
                String name, ArrayList<CharSequence> value) {
            return (ActivityIntentBuilder) super.putCharSequenceArrayListExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, Serializable value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, boolean[] value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, byte[] value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, short[] value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, char[] value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, int[] value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, long[] value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, float[] value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, double[] value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, String[] value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, CharSequence[] value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtra(String name, Bundle value) {
            return (ActivityIntentBuilder) super.putExtra(name, value);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtras(@NonNull Intent src) {
            return (ActivityIntentBuilder) super.putExtras(src);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder putExtras(@NonNull Bundle extras) {
            return (ActivityIntentBuilder) super.putExtras(extras);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder replaceExtras(@NonNull Intent src) {
            return (ActivityIntentBuilder) super.replaceExtras(src);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder replaceExtras(@NonNull Bundle extras) {
            return (ActivityIntentBuilder) super.replaceExtras(extras);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setFlags(int flags) {
            return (ActivityIntentBuilder) super.setFlags(flags);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder addFlags(int flags) {
            return (ActivityIntentBuilder) super.addFlags(flags);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setPackage(@Nullable String packageName) {
            return (ActivityIntentBuilder) super.setPackage(packageName);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setComponent(@Nullable ComponentName component) {
            return (ActivityIntentBuilder) super.setComponent(component);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setClassName(
                @NonNull Context packageContext, @NonNull String className) {
            return (ActivityIntentBuilder) super.setClassName(packageContext, className);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setClassName(
                @NonNull String packageName, @NonNull String className) {
            return (ActivityIntentBuilder) super.setClassName(packageName, className);
        }

        @NonNull
        @Override
        public ActivityIntentBuilder setClass(
                @NonNull Context packageContext, @NonNull Class<?> cls) {
            return (ActivityIntentBuilder) super.setClass(packageContext, cls);
        }
    }

    public static class FragmentTransactionBuilder {

        private final FragmentManager mManager;
        private final int mContainerViewId;
        private final Bundle mExtras = new Bundle();
        private FragmentTransaction mTransaction;

        public FragmentTransactionBuilder(
                @NonNull FragmentActivity activity, @IdRes int containerViewId) {
            this.mContainerViewId = containerViewId;
            mManager = activity.getSupportFragmentManager();
        }

        /**
         * 打开Fragment
         *
         * @param pagePath 要显示的Fragment页面路径
         */
        @SuppressWarnings("unchecked")
        public void open(String pagePath) {
            if (isFragment(pagePath)) {
                //添加参数
                putExtras(Router.parseUri(Uri.parse(pagePath)))
                        .openIgnoredException((Class<? extends Fragment>)
                                RouterHolder.INSTANCE.mPages.get(pagePath), pagePath);
            }
        }

        /**
         * 打开Fragment
         *
         * @param fragment 要显示的Fragment
         * @param tag      {@link FragmentManager#findFragmentByTag(String)}
         */
        public void open(Fragment fragment, @Nullable String tag) {
            Bundle arguments = fragment.getArguments();
            //创建fragment实例
            if (arguments != null) {
                mExtras.putAll(arguments);
            }
            fragment.setArguments(mExtras);
            if (mTransaction == null) {
                mTransaction = mManager.beginTransaction();
            }
            //设置fragment过渡动画
            mTransaction.setCustomAnimations(RouterHolder.INSTANCE.mEnter, RouterHolder.INSTANCE.mExit,
                    RouterHolder.INSTANCE.mPopEnter, RouterHolder.INSTANCE.mPopExit);
            if (TextUtils.isEmpty(tag) || mManager.findFragmentByTag(tag) == null) {
                mTransaction.add(mContainerViewId, fragment, tag);
            }
            mTransaction.show(fragment).commitAllowingStateLoss();
        }

        /**
         * 打开Fragment
         *
         * @param fClass Fragment.class
         * @param tag    {@link FragmentManager#findFragmentByTag(String)}
         * @throws InstantiationException if this {@code Class} represents an abstract class,
         *                                an interface, an array class, a primitive type, or void;
         *                                or if the class has no nullary constructor;
         *                                or if the instantiation fails for some other reason.
         * @throws IllegalAccessException if the class or its nullary
         *                                constructor is not accessible.
         */
        public void open(Class<? extends Fragment> fClass, @Nullable String tag)
                throws InstantiationException, IllegalAccessException {
            open(fClass.newInstance(), tag);
        }

        /**
         * 忽略创建异常打开Fragment
         *
         * @param fClass Fragment.class
         * @param tag    {@link FragmentManager#findFragmentByTag(String)}
         */
        public void openIgnoredException(Class<? extends Fragment> fClass, @Nullable String tag) {
            try {
                open(fClass, tag);
            } catch (InstantiationException ignored) {
            } catch (IllegalAccessException ignored) {
            }
        }

        @NonNull
        public FragmentTransactionBuilder replaceTransaction(FragmentTransaction transaction) {
            mTransaction = transaction;
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, boolean value) {
            mExtras.putBoolean(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, byte value) {
            mExtras.putByte(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, char value) {
            mExtras.putChar(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, short value) {
            mExtras.putShort(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, int value) {
            mExtras.putInt(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, long value) {
            mExtras.putLong(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, float value) {
            mExtras.putFloat(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, double value) {
            mExtras.putDouble(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, String value) {
            mExtras.putString(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, CharSequence value) {
            mExtras.putCharSequence(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, Parcelable value) {
            mExtras.putParcelable(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, Parcelable[] value) {
            mExtras.putParcelableArray(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putParcelableArrayListExtra(
                String name, ArrayList<? extends Parcelable> value) {
            mExtras.putParcelableArrayList(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putIntegerArrayListExtra(
                String name, ArrayList<Integer> value) {
            mExtras.putIntegerArrayList(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putStringArrayListExtra(
                String name, ArrayList<String> value) {
            mExtras.putStringArrayList(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putCharSequenceArrayListExtra(
                String name, ArrayList<CharSequence> value) {
            mExtras.putCharSequenceArrayList(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, Serializable value) {
            mExtras.putSerializable(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, boolean[] value) {
            mExtras.putBooleanArray(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, byte[] value) {
            mExtras.putByteArray(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, short[] value) {
            mExtras.putShortArray(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, char[] value) {
            mExtras.putCharArray(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, int[] value) {
            mExtras.putIntArray(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, long[] value) {
            mExtras.putLongArray(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, float[] value) {
            mExtras.putFloatArray(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, double[] value) {
            mExtras.putDoubleArray(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, String[] value) {
            mExtras.putStringArray(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtra(String name, CharSequence[] value) {
            mExtras.putCharSequenceArray(name, value);
            return this;
        }

        @NonNull
        public FragmentTransactionBuilder putExtras(Bundle extras) {
            if (extras != null) {
                mExtras.putAll(extras);
            }
            return this;
        }

    }

    public interface RouterInterceptor {
        /**
         * Router 拦截回调
         *
         * @param pagePath 页面路径
         * @return true拦截，false不拦截
         */
        boolean intercept(String pagePath);
    }
}
