package dev.yong.wheel

import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author coderyong
 */
class ThreadExecutor private constructor() : Executor {

    private val mPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(
        CPU_COUNT + 1, CPU_COUNT * 2 + 1,
        500L, TimeUnit.MILLISECONDS,
        LinkedBlockingDeque()
    )

    override fun execute(command: Runnable) {
        mPoolExecutor.execute(command)
    }

    init {
        mPoolExecutor.allowCoreThreadTimeOut(true)
    }

    private object ThreadExecutorHolder {
        val mInstance: ThreadExecutor = ThreadExecutor()
    }

    companion object {

        @JvmStatic
        fun getInstance(): ThreadExecutor {
            return ThreadExecutorHolder.mInstance
        }

        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    }
}