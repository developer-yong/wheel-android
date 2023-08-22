@file:Suppress("unused")

package dev.yong.wheel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.Process
import android.widget.Toast
import dev.yong.wheel.utils.Logger
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import kotlin.system.exitProcess

/**
 * @author coderyong
 */
class AppManager private constructor() : Thread.UncaughtExceptionHandler,
    ActivityLifecycleCallbacks {

    private var mApplication: Application? = null

    private val mActivities: Vector<Activity> = Vector()
    private val mLifecycleCallbacks: CopyOnWriteArrayList<ActivityLifecycleCallbacks> =
        CopyOnWriteArrayList()

    val application: Application
        get() {
            checkNotNull(mApplication) { "Application not initialized, AppManager.init(Application) not implemented" }
            return mApplication!!
        }

    /**
     * 获取应用信息
     *
     * @return PackageInfo
     */
    val packageInfo: PackageInfo?
        get() {
            return try {
                val packageName = application.packageName
                application.packageManager.getPackageInfo(packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

    /**
     * 获取栈顶的Activity
     *
     * @return Activity
     */
    val topActivity: Activity?
        get() = if (mActivities.size > 0) {
            mActivities[mActivities.size - 1]
        } else {
            null
        }

    /**
     * 获取上一个Activity
     *
     * @return Activity
     */
    val preActivity: Activity?
        get() = if (mActivities.size > 1) {
            mActivities[mActivities.size - 2]
        } else {
            null
        }

    /**
     * 添加Activity生命周期监听
     */
    fun addActivityLifecycleListener(lifecycleCallback: ActivityLifecycleCallbacks) {
        try {
            val iterator = mLifecycleCallbacks.iterator()
            while (iterator.hasNext()) {
                val callback = iterator.next()
                if (callback.javaClass.simpleName
                        .contains(lifecycleCallback.javaClass.simpleName)
                ) {
                    iterator.remove()
                }
            }
            mLifecycleCallbacks.add(lifecycleCallback)
        } catch (ignored: Throwable) {
        }
    }

    /**
     * 移除Activity生命周期监听
     */
    fun removeActivityLifecycleListener(lifecycleCallback: ActivityLifecycleCallbacks): AppManager {
        mLifecycleCallbacks.remove(lifecycleCallback)
        return this
    }

    /**
     * 添加Activity
     */
    private fun addActivity(activity: Activity) {
        if (!mActivities.contains(activity)) {
            mActivities.add(activity)
        }
    }

    /**
     * 销毁所有Activity
     */
    private fun clearAllActivity() {
        synchronized(AppManager::class.java) {
            val iterator: MutableIterator<Activity> = mActivities.iterator()
            while (iterator.hasNext()) {
                val a: Activity = iterator.next()
                iterator.remove()
                a.finish()
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun restart(intent: Intent? = null) {
        mApplication?.let {
            var rIntent = intent
            if (rIntent == null) {
                rIntent = it.packageManager.getLaunchIntentForPackage(it.packageName)
            }
            rIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            rIntent?.putExtra("restart", true)
            it.startActivity(rIntent)
            exit()
        }
    }

    /**
     * 退出当前应用程序
     */
    fun exit() {
        clearAllActivity()
        mActivities.clear()
        mLifecycleCallbacks.clear()
        mApplication = null
        //退出程序
        Process.killProcess(Process.myPid())
        System.gc()
        exitProcess(0)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        captureException(t, e)
        showToast()
        restart()
    }

    /**
     * 捕获异常信息
     *
     * @param t  奔溃线程
     * @param ex 奔溃异常
     */
    @Suppress("DEPRECATION")
    private fun captureException(t: Thread, ex: Throwable) {
        val map = LinkedHashMap<String, String>()
        val info = packageInfo
        map["手机品牌"] = "" + Build.BRAND
        map["手机型号"] = "" + Build.MODEL
        map["SDK版本"] = "" + Build.VERSION.SDK_INT
        map["versionName"] = info!!.versionName
        map["versionCode"] = "" + info.versionCode
        val msg = """${t.name} Thread异常崩溃
            |设备信息：$map""".trimMargin()
        if (BuildConfig.DEBUG) {
            Logger.e(ex, t.name)
        } else {
            Logger.file(mApplication!!, msg, ex)
        }
    }

    private fun showToast() {
        Executors.newSingleThreadExecutor().execute {
            Looper.prepare()
            Toast.makeText(mApplication!!, "很抱歉，程序出现异常，即将重启", Toast.LENGTH_LONG).show()
            Looper.loop()
        }
        try {
            //Toast展示的时间
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            Logger.e(e, "AppRestart")
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        addActivity(activity)
        for (callback in mLifecycleCallbacks) {
            callback.onActivityCreated(activity, savedInstanceState)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        addActivity(activity)
        for (callback in mLifecycleCallbacks) {
            callback.onActivityStarted(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        addActivity(activity)
        for (callback in mLifecycleCallbacks) {
            callback.onActivityResumed(activity)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        for (callback in mLifecycleCallbacks) {
            callback.onActivityPaused(activity)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        for (callback in mLifecycleCallbacks) {
            callback.onActivityStopped(activity)
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        mActivities.remove(activity)
        for (callback in mLifecycleCallbacks) {
            callback.onActivityDestroyed(activity)
            if (mActivities.isEmpty() && callback is SimpleActivityLifecycleCallbacks) {
                callback.onAllActivityDestroyed()
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, savedInstanceState: Bundle) {
        for (callback in mLifecycleCallbacks) {
            callback.onActivitySaveInstanceState(activity, savedInstanceState)
        }
    }

    private object AppManagerHolder {
        val mInstance: AppManager = AppManager()
    }

    interface SimpleActivityLifecycleCallbacks : ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

        fun onAllActivityDestroyed() {}
    }

    companion object {

        @JvmStatic
        fun getInstance(): AppManager {
            return AppManagerHolder.mInstance
        }

        /**
         * 初始化App
         *
         * @param application Application
         */
        @JvmStatic
        fun init(application: Application) {
            val instance = getInstance()
            instance.mApplication = application
            //设置该CrashHandler为程序的默认处理器
            Thread.setDefaultUncaughtExceptionHandler(instance)
            application.registerActivityLifecycleCallbacks(instance)
        }
    }
}