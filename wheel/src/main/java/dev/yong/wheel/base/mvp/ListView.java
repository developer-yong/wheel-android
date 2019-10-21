package dev.yong.wheel.base.mvp;

import java.util.List;

/**
 * @author coderyong
 */
public interface ListView<T> extends MessageView {
    /**
     * 显示列表内容
     *
     * @param isRefresh 是否为刷新内容
     * @param tList     列表信息
     */
    default void showList(boolean isRefresh, List<T> tList) {
    }
}
