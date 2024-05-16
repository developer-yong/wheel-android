package dev.yong.wheel

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.SparseArray
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.max

/**
 * @author coderyong
 */
class EventHandler private constructor() : Handler(Looper.getMainLooper()) {

    /**
     * 回调集合
     */
    private val mCallbacks: CopyOnWriteArrayList<OnEventCallback> = CopyOnWriteArrayList()

    /**
     * 事件结束时间
     */
    private val mEventEndTimes = SparseArray<Long>()

    /**
     * 事件携带数据
     */
    private val mEventData = SparseArray<Any>()

    /**
     * 事件执行时间
     */
    private val mEventTimes = SparseArray<Long>()

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        for (callback in mCallbacks) {
            //获取事件执行时间
            val delayMillis = mEventTimes[msg.what, -1L]
            if (delayMillis > 0) {
                //循环事件
                send(msg.what, delayMillis, true, mEventData[msg.what])
            }
            callback.onEvent(msg.what, mEventData[msg.what])
        }
    }

    /**
     * 事件订阅
     * <P>
     *      调用该方法订阅事件，事件执行会在[.send]delayMillis后，
     *      回调[OnEventCallback.onEvent]，收到回调后执行相应操作即可。
     * </P>
     *
     * @param callback 事件回调
     */
    fun subscribe(callback: OnEventCallback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback)
        }
    }

    /**
     * 取消订阅
     * <P>
     *      调用该方法取消事件订阅，避免内存泄露，同时也不会再收到[OnEventCallback.onEvent]回调。
     * </P>
     *
     * @param callback 事件回调
     */
    fun unsubscribe(callback: OnEventCallback) {
        if (mCallbacks.contains(callback)) {
            mCallbacks.remove(callback)
        }
    }

    /**
     * 发送事件
     * <P>
     *      调用该方法前，注意先调用[.subscribe]订阅事件回调，
     *      否则当delayMillis设置为0时，可能会导致无事件回调触发。
     * </P>
     *
     * @param what        事件标识，同[Message.what]用法相同
     * @param delayMillis 延时时间，[Handler.sendEmptyMessageDelayed]
     * @param isLoop      是否循环此次事件
     * @param data        事件所携带数据，如没有可设置为null
     */
    fun send(what: Int, delayMillis: Long = 0, isLoop: Boolean = false, data: Any? = null) {
        removeMessages(what)
        sendEmptyMessageDelayed(what, delayMillis)
        mEventEndTimes.put(what, System.currentTimeMillis() + delayMillis)
        mEventData.put(what, data)
        if (isLoop) {
            mEventTimes.put(what, delayMillis)
        }
    }

    /**
     * 继续事件
     * <P>
     *      当事件被调用[.pause]暂停后，调用该方法恢复事件执行。
     * </P>
     *
     * @param what    事件标识，同[Message.what]用法相同
     * @param newData 事件所携带数据，如没有可设置为null
     */
    fun resume(what: Int, newData: Any? = null) {
        if (has(what)) {
            return
        }
        //获取事件剩余时间
        val endTime = mEventEndTimes[what, -1L]
        //重新设置本次事件结束时间
        mEventEndTimes.put(what, System.currentTimeMillis() + endTime)
        mEventData.put(what, newData)
        removeMessages(what)
        sendEmptyMessageDelayed(what, endTime)
    }

    /**
     * 暂停事件
     * <P>
     *      当事件需要在特定场景下暂停一段时间，调用该方法实现事件暂停。
     *      如果在暂停中，调用了[.cancel]则不会恢复。
     * </P>
     *
     * @param what    事件标识，同[Message.what]用法相同
     */
    fun pause(what: Int) {
        //获取此事件结束时间
        var endTime = mEventEndTimes[what, -1L]
        //移除事件并记录事件剩余时间
        endTime -= System.currentTimeMillis()
        mEventEndTimes.put(what, max(0, endTime))
        removeMessages(what)
    }

    /**
     * 获取事件剩余时间
     * <P>
     *      在事件暂停后调用该方法可获取事件剩余时间
     * </P>
     *
     * @param what 事件标识，同[Message.what]用法相同
     * @return 返回事件暂停后剩余时间
     */
    fun getTimeRemaining(what: Int): Long {
        return mEventEndTimes[what] ?: return -1
    }

    /**
     * 是否存在事件
     *
     * @param what 事件标识
     */
    fun has(what: Int): Boolean {
        return hasMessages(what)
    }

    /**
     * 取消事件
     *
     * @param what 事件标识，为空取消所有事件
     */
    fun cancel(what: Int) {
        removeMessages(what)
        mEventTimes.remove(what)
        mEventEndTimes.remove(what)
        mEventData.remove(what)
    }

    /**
     * 取消所有事件
     */
    fun cancelAll() {
        removeCallbacksAndMessages(null)
        mCallbacks.clear()
        mEventTimes.clear()
        mEventEndTimes.clear()
        mEventData.clear()
    }

    interface OnEventCallback {
        fun onEvent(what: Int, data: Any?)
    }

    private object EventHandlerHolder {
        val mInstance: EventHandler = EventHandler()
    }

    companion object {
        @JvmStatic
        fun getInstance(): EventHandler {
            return EventHandlerHolder.mInstance
        }
    }
}