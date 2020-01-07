package dev.yong.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import java.util.List;

import dev.yong.photo.bean.MediaFile;
import dev.yong.photo.compress.CompressFactory;

/**
 * @author coderyong
 */
public class PhotoSelector {

    private PhotoManager mManager;

    private boolean isFinish = false;
    private OnSelectedListener mSelectedListener;

    @SuppressLint("StaticFieldLeak")
    private static PhotoSelector sInstance = null;

    public static PhotoSelector getInstance() {
        if (sInstance == null) {
            synchronized (PhotoSelector.class) {
                if (sInstance == null) {
                    sInstance = new PhotoSelector();
                }
            }
        }
        return sInstance;
    }

    private PhotoSelector() {
        mManager = new PhotoManager();
    }

    public void select(Activity activity, OnSelectedListener listener) {
        isFinish = false;
        mSelectedListener = listener;
        mManager.build(activity);
        activity.startActivity(new Intent(activity, SelectorActivity.class));
    }

    void cancel() {
        mManager.reset();
        isFinish = true;
    }

    void finish() {
        mManager.finish(mSelectedListener);
        isFinish = true;
    }

    boolean isFinish() {
        return isFinish;
    }

    /**
     * 配置媒体文件类型
     *
     * @param mediaType 默认视频和图片
     */
    public PhotoSelector mediaType(MediaFile.Type mediaType) {
        mManager.setMediaType(mediaType);
        return this;
    }

    /**
     * 配置是否启用相机
     *
     * @param enable 默认false
     */
    public PhotoSelector enableCamera(boolean enable) {
        mManager.setEnableCamera(enable);
        return this;
    }

    /**
     * 配置是否启用裁剪
     *
     * @param enable 默认true
     */
    public PhotoSelector enableEdit(boolean enable) {
        mManager.setEnableEdit(enable);
        return this;
    }

    /**
     * 配置是否压缩
     *
     * @param enable 默认true
     */
    public PhotoSelector enableCompress(boolean enable) {
        mManager.setEnableCompress(enable);
        return this;
    }

    /**
     * 配置压缩工厂实现
     *
     * @param compressFactory 压缩工厂实现类
     */
    public PhotoSelector compressFactory(CompressFactory compressFactory) {
        mManager.setCompressFactory(compressFactory);
        return this;
    }

    /**
     * 配置最大选择数量
     *
     * @param maxCount 默认9张
     */
    public PhotoSelector maxSelectCount(int maxCount) {
        mManager.setMaxSelectCount(maxCount);
        return this;
    }

    /**
     * 配置最小文件字节数
     *
     * @param byteSize 默认1024B
     */
    public PhotoSelector minFileSize(int byteSize) {
        mManager.setMinFileSize(byteSize);
        return this;
    }

    /**
     * 配置最大文件字节数
     *
     * @param byteSize 默认10485760B
     */
    public PhotoSelector maxFileSize(int byteSize) {
        mManager.setMaxFileSize(byteSize);
        return this;
    }

    boolean enableCamera() {
        return mManager.isEnableCamera();
    }

    boolean enableEdit() {
        return mManager.isEnableEdit();
    }

    boolean enableCompress() {
        return mManager.isEnableCompress();
    }

    MediaFile.Type mediaType() {
        return mManager.getMediaType();
    }

    public int maxSelectCount() {
        return mManager.getMaxSelectCount();
    }

    public int selectedCount() {
        return mManager.selectedCount();
    }

    List<MediaFile> getMediaFiles() {
        return mManager.getMediaFiles();
    }
}
