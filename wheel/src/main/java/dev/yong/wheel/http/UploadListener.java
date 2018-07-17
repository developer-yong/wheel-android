package dev.yong.wheel.http;

/**
 * @author coderyong
 */
public interface UploadListener {
    /**
     * 进度回调
     *
     * @param currentLength 当前长度
     * @param totalLength   总长度
     * @param done          是否完成或失效
     */
    void onProgress(long currentLength, long totalLength, boolean done);
}
