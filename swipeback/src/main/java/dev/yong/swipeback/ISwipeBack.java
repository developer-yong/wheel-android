package dev.yong.swipeback;

import android.app.Activity;
import android.view.View;

/**
 * 滑动返回接口类
 * <P>
 *     该类只能被Activity实现
 * </P>
 * @author coderyong
 */
public interface ISwipeBack {

    /**
     * 上一个视图
     * <p>
     *      可以通过上一个Activity{@link Activity#getWindow()#getDecorView()}获得
     *      该方法返回null时与{@link SwipeBackLayout#setPrevViewScrollable(boolean)} 设为false等同
     * </P>
     *
     * @return View视图
     */
    View prevView();

    /**
     * 是否支持滑动返回
     *
     * @return true支持, false不支持
     */
    boolean isSupportSwipeBack();
}
