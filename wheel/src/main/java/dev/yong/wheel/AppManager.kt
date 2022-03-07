@file:Suppress("unused")

package dev.yong.wheel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
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
import java.util.concurrent.Executors
import kotlin.system.exitProcess

/**
 * @author coderyong
 */
class AppManager private constructor() : Thread.UncaughtExceptionHandler,
    Application.ActivityLifecycleCallbacks {
    private var mApplication: Application? = null
    private var mMainClazz: Class<*>? = null
    var isUseEventBus = false
    private var mActivities: Vector<Activity> = Vector<Activity>()
    val application: Application
        get() {
            checkNotNull(mApplication) { "Application not initialized, AppManager.init(Application) not implemented" }
            return mApplication!!
        }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun uncaughtException(t: Thread, e: Throwable) {
        showToast()
        captureException(t, e)
        val intent = Intent(mApplication, mMainClazz)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val restartIntent: PendingIntent = PendingIntent.getActivity(
            mApplication, 0, intent, PendingIntent.FLAG_ONE_SHOT
        )
        //重启应用
        val manager: AlarmManager =
            (mApplication!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        manager.set(AlarmManager.RTC, System.currentTimeMillis(), restartIntent)
        //退出
        exit()
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

    /**
     * 退出当前应用程序
     */
    private fun exit() {
//        EventBus.getDefault().clear()
        clearAllActivity()
        mActivities.clear()
        mMainClazz = null
        mApplication = null
        //退出程序
        Process.killProcess(Process.myPid())
        System.gc()
        exitProcess(0)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (mActivities.indexOf(activity) == -1) {
            mActivities.add(activity)
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        mActivities.remove(activity)
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {}

    companion object {
        @JvmStatic
        val instance = AppManager()

        /**
         * 初始化App
         *
         * @param application Application
         * @param mainClazz   如有奔溃需要启动的Activity
         */
        @JvmStatic
        fun init(application: Application, mainClazz: Class<*>? = null) {
            instance.mApplication = application
            if (mainClazz != null) {
                instance.mMainClazz = mainClazz
                //设置该CrashHandler为程序的默认处理器
                Thread.setDefaultUncaughtExceptionHandler(instance)
            }
            application.registerActivityLifecycleCallbacks(instance)
        }
    }
}