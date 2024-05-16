package dev.yong.wheel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.io.Serializable
import java.util.*

/**
 * @author coderyong
 */
class Router private constructor() {
    /**
     * Router页面集合
     */
    private val mPages = HashMap<String, Class<*>>()

    /**
     * Fragment过渡动画
     */
    private var mEnter = R.anim.slide_in_right
    private var mExit = R.anim.slide_out_left
    private var mPopEnter = R.anim.slide_in_left
    private var mPopExit = R.anim.slide_out_right

    /**
     * Router动作监听
     */
    private var mInterceptor: RouterInterceptor? = null
    private var mApp: Application? = null

    /**
     * 当前正在运行的Activity
     */
    private var mRunningActivity: Activity? = null

    private object RouterHolder {
        @SuppressLint("StaticFieldLeak")
        val INSTANCE = Router()
    }

    class ActivityIntentBuilder(private val mContext: Context) : Intent() {
        private val mRouter = RouterHolder.INSTANCE

        /**
         * 启动Activity页面
         *
         * 如果需要处理特殊路由[Router.init],
         * 当未初始化[RouterInterceptor]且页面未被注册[Router.registerActivityPage]时,
         * 使用此方法不会抛出异常，同时也不会有新页面启动；
         * 当启动页面已注册且被[RouterInterceptor.intercept]
         * 拦截处理返回false后页面仍会正常启动，反之只执行拦截处理内容
         *
         * @param pagePath 页面路径
         */
        fun start(pagePath: String) {
            if (mRouter.mInterceptor != null) {
                if (mRouter.mInterceptor!!.intercept(pagePath)) {
                    return
                }
            }
            if (isActivity(pagePath)) {
                mRouter.mPages[pagePath]?.run { setClass(mContext, this) }
                //添加参数
                putExtras(parseUri(Uri.parse(pagePath)))
                mContext.startActivity(this)
            }
        }

        /**
         * 启动Activity
         *
         * @param aClass Activity.class
         */
        fun start(aClass: Class<*>) {
            setClass(mContext, aClass)
            mContext.startActivity(this)
        }

        override fun setAction(action: String?): ActivityIntentBuilder {
            return super.setAction(action) as ActivityIntentBuilder
        }

        override fun setData(data: Uri?): ActivityIntentBuilder {
            return super.setData(data) as ActivityIntentBuilder
        }

        override fun setDataAndNormalize(data: Uri): ActivityIntentBuilder {
            return super.setDataAndNormalize(data) as ActivityIntentBuilder
        }

        override fun setType(type: String?): ActivityIntentBuilder {
            return super.setType(type) as ActivityIntentBuilder
        }

        override fun setTypeAndNormalize(type: String?): ActivityIntentBuilder {
            return super.setTypeAndNormalize(type) as ActivityIntentBuilder
        }

        override fun setDataAndType(data: Uri?, type: String?): ActivityIntentBuilder {
            return super.setDataAndType(data, type) as ActivityIntentBuilder
        }

        override fun setDataAndTypeAndNormalize(
            data: Uri, type: String?
        ): ActivityIntentBuilder {
            return super.setDataAndTypeAndNormalize(data, type) as ActivityIntentBuilder
        }

        override fun addCategory(category: String): ActivityIntentBuilder {
            return super.addCategory(category) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Boolean): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Byte): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Char): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Short): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Int): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Long): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Float): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Double): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: String?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: CharSequence?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Parcelable?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Array<Parcelable>?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putParcelableArrayListExtra(
            name: String, value: ArrayList<out Parcelable>?
        ): ActivityIntentBuilder {
            return super.putParcelableArrayListExtra(name, value) as ActivityIntentBuilder
        }

        override fun putIntegerArrayListExtra(
            name: String,
            value: ArrayList<Int>?
        ): ActivityIntentBuilder {
            return super.putIntegerArrayListExtra(name, value) as ActivityIntentBuilder
        }

        override fun putStringArrayListExtra(
            name: String,
            value: ArrayList<String>?
        ): ActivityIntentBuilder {
            return super.putStringArrayListExtra(name, value) as ActivityIntentBuilder
        }

        override fun putCharSequenceArrayListExtra(
            name: String, value: ArrayList<CharSequence>?
        ): ActivityIntentBuilder {
            return super.putCharSequenceArrayListExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Serializable?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: BooleanArray?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: ByteArray?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: ShortArray?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: CharArray?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: IntArray?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: LongArray?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: FloatArray?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: DoubleArray?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Array<String>?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Array<CharSequence>?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtra(name: String, value: Bundle?): ActivityIntentBuilder {
            return super.putExtra(name, value) as ActivityIntentBuilder
        }

        override fun putExtras(src: Intent): ActivityIntentBuilder {
            return super.putExtras(src) as ActivityIntentBuilder
        }

        override fun putExtras(extras: Bundle): ActivityIntentBuilder {
            return super.putExtras(extras) as ActivityIntentBuilder
        }

        override fun replaceExtras(src: Intent): ActivityIntentBuilder {
            return super.replaceExtras(src) as ActivityIntentBuilder
        }

        override fun replaceExtras(extras: Bundle?): ActivityIntentBuilder {
            return super.replaceExtras(extras) as ActivityIntentBuilder
        }

        override fun setFlags(flags: Int): ActivityIntentBuilder {
            return super.setFlags(flags) as ActivityIntentBuilder
        }

        override fun addFlags(flags: Int): ActivityIntentBuilder {
            return super.addFlags(flags) as ActivityIntentBuilder
        }

        override fun setPackage(packageName: String?): ActivityIntentBuilder {
            return super.setPackage(packageName) as ActivityIntentBuilder
        }

        override fun setComponent(component: ComponentName?): ActivityIntentBuilder {
            return super.setComponent(component) as ActivityIntentBuilder
        }

        override fun setClassName(
            packageContext: Context, className: String
        ): ActivityIntentBuilder {
            return super.setClassName(packageContext, className) as ActivityIntentBuilder
        }

        override fun setClassName(
            packageName: String, className: String
        ): ActivityIntentBuilder {
            return super.setClassName(packageName, className) as ActivityIntentBuilder
        }

        override fun setClass(
            packageContext: Context, cls: Class<*>
        ): ActivityIntentBuilder {
            return super.setClass(packageContext, cls) as ActivityIntentBuilder
        }
    }

    class FragmentTransactionBuilder(
        activity: FragmentActivity, @param:IdRes private val mContainerViewId: Int
    ) {
        private val mManager: FragmentManager
        private val mExtras = Bundle()
        private var mTransaction: FragmentTransaction? = null

        init {
            mManager = activity.supportFragmentManager
        }

        /**
         * 打开Fragment
         *
         * @param pagePath 要显示的Fragment页面路径
         */
        @Suppress("UNCHECKED_CAST")
        fun open(
            pagePath: String,
            @AnimatorRes @AnimRes enter: Int = RouterHolder.INSTANCE.mEnter,
            @AnimatorRes @AnimRes exit: Int = RouterHolder.INSTANCE.mExit
        ) {
            if (isFragment(pagePath)) {
                //添加参数
                putExtras(parseUri(Uri.parse(pagePath)))
                    .openIgnoredException(
                        RouterHolder.INSTANCE.mPages[pagePath] as Class<out Fragment>,
                        pagePath,
                        enter,
                        exit
                    )
            }
        }

        /**
         * 打开Fragment
         *
         * @param fragment 要显示的Fragment
         * @param tag      [FragmentManager.findFragmentByTag]
         */
        fun open(
            fragment: Fragment, tag: String?,
            @AnimatorRes @AnimRes enter: Int = RouterHolder.INSTANCE.mEnter,
            @AnimatorRes @AnimRes exit: Int = RouterHolder.INSTANCE.mExit
        ) {
            val arguments = fragment.arguments
            //创建fragment实例
            if (arguments != null) {
                mExtras.putAll(arguments)
            }
            fragment.arguments = mExtras
            if (mTransaction == null) {
                mTransaction = mManager.beginTransaction()
            }
            //设置fragment过渡动画
            mTransaction!!.setCustomAnimations(
                enter, exit,
                RouterHolder.INSTANCE.mPopEnter, RouterHolder.INSTANCE.mPopExit
            )
            if (TextUtils.isEmpty(tag) || mManager.findFragmentByTag(tag) == null) {
                mTransaction!!.add(mContainerViewId, fragment, tag)
            }
            mTransaction!!.show(fragment).commitAllowingStateLoss()
        }

        /**
         * 打开Fragment
         *
         * @param fClass Fragment.class
         * @param tag    [FragmentManager.findFragmentByTag]
         * @throws InstantiationException if this `Class` represents an abstract class,
         * an interface, an array class, a primitive type, or void;
         * or if the class has no nullary constructor;
         * or if the instantiation fails for some other reason.
         * @throws IllegalAccessException if the class or its nullary
         * constructor is not accessible.
         */
        @Throws(InstantiationException::class, IllegalAccessException::class)
        fun open(
            fClass: Class<out Fragment>, tag: String? = "",
            @AnimatorRes @AnimRes enter: Int = RouterHolder.INSTANCE.mEnter,
            @AnimatorRes @AnimRes exit: Int = RouterHolder.INSTANCE.mExit
        ) {
            open(fClass.newInstance(), tag, enter, exit)
        }

        /**
         * 忽略创建异常打开Fragment
         *
         * @param fClass Fragment.class
         * @param tag    [FragmentManager.findFragmentByTag]
         */
        private fun openIgnoredException(
            fClass: Class<out Fragment>, tag: String?,
            @AnimatorRes @AnimRes enter: Int = RouterHolder.INSTANCE.mEnter,
            @AnimatorRes @AnimRes exit: Int = RouterHolder.INSTANCE.mExit
        ) {
            try {
                open(fClass, tag, enter, exit)
            } catch (ignored: InstantiationException) {
            } catch (ignored: IllegalAccessException) {
            }
        }

        fun replaceTransaction(transaction: FragmentTransaction?): FragmentTransactionBuilder {
            mTransaction = transaction
            return this
        }

        fun putExtra(name: String?, value: Boolean): FragmentTransactionBuilder {
            mExtras.putBoolean(name, value)
            return this
        }

        fun putExtra(name: String?, value: Byte): FragmentTransactionBuilder {
            mExtras.putByte(name, value)
            return this
        }

        fun putExtra(name: String?, value: Char): FragmentTransactionBuilder {
            mExtras.putChar(name, value)
            return this
        }

        fun putExtra(name: String?, value: Short): FragmentTransactionBuilder {
            mExtras.putShort(name, value)
            return this
        }

        fun putExtra(name: String?, value: Int): FragmentTransactionBuilder {
            mExtras.putInt(name, value)
            return this
        }

        fun putExtra(name: String?, value: Long): FragmentTransactionBuilder {
            mExtras.putLong(name, value)
            return this
        }

        fun putExtra(name: String?, value: Float): FragmentTransactionBuilder {
            mExtras.putFloat(name, value)
            return this
        }

        fun putExtra(name: String?, value: Double): FragmentTransactionBuilder {
            mExtras.putDouble(name, value)
            return this
        }

        fun putExtra(name: String?, value: String?): FragmentTransactionBuilder {
            mExtras.putString(name, value)
            return this
        }

        fun putExtra(name: String?, value: CharSequence?): FragmentTransactionBuilder {
            mExtras.putCharSequence(name, value)
            return this
        }

        fun putExtra(name: String?, value: Parcelable?): FragmentTransactionBuilder {
            mExtras.putParcelable(name, value)
            return this
        }

        fun putExtra(name: String?, value: Array<Parcelable?>?): FragmentTransactionBuilder {
            mExtras.putParcelableArray(name, value)
            return this
        }

        fun putParcelableArrayListExtra(
            name: String?, value: ArrayList<out Parcelable?>?
        ): FragmentTransactionBuilder {
            mExtras.putParcelableArrayList(name, value)
            return this
        }

        fun putIntegerArrayListExtra(
            name: String?, value: ArrayList<Int?>?
        ): FragmentTransactionBuilder {
            mExtras.putIntegerArrayList(name, value)
            return this
        }

        fun putStringArrayListExtra(
            name: String?, value: ArrayList<String?>?
        ): FragmentTransactionBuilder {
            mExtras.putStringArrayList(name, value)
            return this
        }

        fun putCharSequenceArrayListExtra(
            name: String?, value: ArrayList<CharSequence?>?
        ): FragmentTransactionBuilder {
            mExtras.putCharSequenceArrayList(name, value)
            return this
        }

        fun putExtra(name: String?, value: Serializable?): FragmentTransactionBuilder {
            mExtras.putSerializable(name, value)
            return this
        }

        fun putExtra(name: String?, value: BooleanArray?): FragmentTransactionBuilder {
            mExtras.putBooleanArray(name, value)
            return this
        }

        fun putExtra(name: String?, value: ByteArray?): FragmentTransactionBuilder {
            mExtras.putByteArray(name, value)
            return this
        }

        fun putExtra(name: String?, value: ShortArray?): FragmentTransactionBuilder {
            mExtras.putShortArray(name, value)
            return this
        }

        fun putExtra(name: String?, value: CharArray?): FragmentTransactionBuilder {
            mExtras.putCharArray(name, value)
            return this
        }

        fun putExtra(name: String?, value: IntArray?): FragmentTransactionBuilder {
            mExtras.putIntArray(name, value)
            return this
        }

        fun putExtra(name: String?, value: LongArray?): FragmentTransactionBuilder {
            mExtras.putLongArray(name, value)
            return this
        }

        fun putExtra(name: String?, value: FloatArray?): FragmentTransactionBuilder {
            mExtras.putFloatArray(name, value)
            return this
        }

        fun putExtra(name: String?, value: DoubleArray?): FragmentTransactionBuilder {
            mExtras.putDoubleArray(name, value)
            return this
        }

        fun putExtra(name: String?, value: Array<String?>?): FragmentTransactionBuilder {
            mExtras.putStringArray(name, value)
            return this
        }

        fun putExtra(name: String?, value: Array<CharSequence?>?): FragmentTransactionBuilder {
            mExtras.putCharSequenceArray(name, value)
            return this
        }

        fun putExtras(extras: Bundle?): FragmentTransactionBuilder {
            if (extras != null) {
                mExtras.putAll(extras)
            }
            return this
        }
    }

    interface RouterInterceptor {
        /**
         * Router 拦截回调
         *
         * @param pagePath 页面路径
         * @return true拦截，false不拦截
         */
        fun intercept(pagePath: String): Boolean
    }

    companion object {
        /**
         * 初始化Router
         *
         * @param application Application
         * @param interceptor Router拦截器
         * [RouterInterceptor.intercept]
         */
        fun init(application: Application, interceptor: RouterInterceptor) {
            application.registerActivityLifecycleCallbacks(
                object : Application.ActivityLifecycleCallbacks {
                    override fun onActivityCreated(
                        activity: Activity, savedInstanceState: Bundle?
                    ) {
                    }

                    override fun onActivityStarted(activity: Activity) {}
                    override fun onActivityResumed(activity: Activity) {
                        RouterHolder.INSTANCE.mRunningActivity = activity
                    }

                    override fun onActivityPaused(activity: Activity) {}
                    override fun onActivityStopped(activity: Activity) {}
                    override fun onActivitySaveInstanceState(
                        activity: Activity, outState: Bundle
                    ) {
                    }

                    override fun onActivityDestroyed(activity: Activity) {
                        if (RouterHolder.INSTANCE.mRunningActivity === activity) {
                            RouterHolder.INSTANCE.mRunningActivity = null
                        }
                    }
                })
            RouterHolder.INSTANCE.mApp = application
            RouterHolder.INSTANCE.mInterceptor = interceptor
        }

        /**
         * 注册Activity页面
         *
         * @param pagePath 页面路径
         * @param aClass   Activity页面
         */
        fun registerActivityPage(pagePath: String, aClass: Class<out Activity>) {
            RouterHolder.INSTANCE.mPages[pagePath] = aClass
        }

        /**
         * 注册Fragment页面
         *
         * @param pagePath 页面路径
         * @param fClass   Fragment页面
         */
        fun registerFragmentPage(pagePath: String, fClass: Class<out Fragment>) {
            RouterHolder.INSTANCE.mPages[pagePath] = fClass
        }

        /**
         * 注册Fragment过渡动画
         * [FragmentTransaction.setCustomAnimations]
         *
         * @param enter    入栈时进入动画
         * @param exit     入栈时退出动画
         * @param popEnter 出栈时进入动画
         * @param popExit  出栈时退出动画
         */
        fun registerFragmentTransition(
            @AnimatorRes @AnimRes enter: Int,
            @AnimatorRes @AnimRes exit: Int,
            @AnimatorRes @AnimRes popEnter: Int,
            @AnimatorRes @AnimRes popExit: Int
        ) {
            RouterHolder.INSTANCE.mEnter = enter
            RouterHolder.INSTANCE.mExit = exit
            RouterHolder.INSTANCE.mPopEnter = popEnter
            RouterHolder.INSTANCE.mPopExit = popExit
        }

        /**
         * 页面是否未注册
         *
         * @param pagePath 页面路径
         * @return true未注册，false已注册
         */
        fun isUnregister(pagePath: String): Boolean {
            return !RouterHolder.INSTANCE.mPages.containsKey(pagePath)
        }

        /**
         * 页面是否是Activity
         *
         * @param pagePath 页面路径
         * @return true为Activity，false为Fragment或者未注册
         */
        fun isActivity(pagePath: String): Boolean {
            val clazz = RouterHolder.INSTANCE.mPages[pagePath]
            return clazz != null && Activity::class.java.isAssignableFrom(clazz)
        }

        /**
         * 页面是否是Fragment
         *
         * @param pagePath 页面路径
         * @return true为Fragment，false为Activity或者未注册
         */
        fun isFragment(pagePath: String): Boolean {
            val clazz = RouterHolder.INSTANCE.mPages[pagePath]
            return clazz != null && Fragment::class.java.isAssignableFrom(clazz)
        }

        fun with(): ActivityIntentBuilder {
            checkNotNull(RouterHolder.INSTANCE.mApp) { "You must use Router.init() for initialization" }
            var withContext: Context? = RouterHolder.INSTANCE.mApp
            if (RouterHolder.INSTANCE.mRunningActivity != null) {
                withContext = RouterHolder.INSTANCE.mRunningActivity
            }
            return ActivityIntentBuilder(withContext!!)
        }

        fun with(context: Context): ActivityIntentBuilder {
            return ActivityIntentBuilder(context)
        }

        fun with(
            activity: FragmentActivity, @IdRes containerViewId: Int
        ): FragmentTransactionBuilder {
            return FragmentTransactionBuilder(activity, containerViewId)
        }

        /**
         * 结束当前Fragment
         *
         * @param fragment 当前显示的Fragment
         * @param enter    进入动画
         * @param exit     退出动画
         */
        @JvmOverloads
        fun finish(
            fragment: Fragment,
            finishActivity: Boolean = true,
            @AnimatorRes @AnimRes enter: Int = RouterHolder.INSTANCE.mPopEnter,
            @AnimatorRes @AnimRes exit: Int = RouterHolder.INSTANCE.mPopExit
        ) {
            try {
                val activity = fragment.requireActivity()
                val manager = activity.supportFragmentManager

                if (finishActivity && manager.fragments.size < 2) {
                    activity.finish()
                    return
                }
                manager.beginTransaction()
                    .setCustomAnimations(enter, exit)
                    .remove(fragment)
                    .commit()
                manager.popBackStackImmediate()
            } catch (_: Throwable) {
            }
        }

        /**
         * 弹窗当前页面
         *
         * @param activity 当前显示的Activity
         * @param enter    进入动画
         * @param exit     退出动画
         */
        @JvmOverloads
        fun popStackImmediate(
            activity: FragmentActivity,
            @AnimatorRes @AnimRes enter: Int = RouterHolder.INSTANCE.mPopEnter,
            @AnimatorRes @AnimRes exit: Int = RouterHolder.INSTANCE.mPopExit
        ) {
            val manager = activity.supportFragmentManager
            //获取当前Activity内所有的Fragment
            val fragments = manager.fragments
            //获取当前显示的Fragment
            var currFragment: Fragment? = null
            for (fragment in fragments) {
                if (fragment.isVisible) {
                    currFragment = fragment
                }
            }
            if (currFragment == null || fragments.size < 2) {
                activity.finish()
            } else {
                manager.beginTransaction()
                    .setCustomAnimations(enter, exit)
                    .remove(currFragment)
                    .commit()
                manager.popBackStackImmediate()
            }
        }

        /**
         * 解析Uri中的参数
         *
         *
         * 如果未解析到参数返回空的Bundle
         *
         *
         * @param uri Uri
         * @return 参数集合Bundle
         */
        fun parseUri(uri: Uri): Bundle {
            val b = Bundle()
            val keys = uri.queryParameterNames
            for (key in keys) {
                b.putString(key, uri.getQueryParameter(key))
            }
            return b
        }
    }
}