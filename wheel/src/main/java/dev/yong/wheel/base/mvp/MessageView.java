package dev.yong.wheel.base.mvp;

/**
 * @author coderyong
 */
public interface MessageView {
    /**
     * 显示成功或错误消息内容
     *
     * @param message 消息内容
     */
    void showMessage(String message);
}
