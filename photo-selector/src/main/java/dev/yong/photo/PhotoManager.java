package dev.yong.photo;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import dev.yong.photo.bean.MediaFile;
import dev.yong.photo.compress.CompressFactory;
import dev.yong.photo.compress.DefaultCompressFactory;

class PhotoManager {

    private Activity mActivity;

    private MediaFile.Type mediaType;
    private boolean enableCamera = false;
    private boolean enableEdit = true;
    private boolean enableCompress = true;
    private int maxSelectCount = 9;
    private int minFileSize = 1024;
    private int maxFileSize = 10485760;

    private CompressFactory mCompressFactory;

    private List<MediaFile> mMediaFiles = new ArrayList<>();

    void build(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity must be not null");
        }
        mActivity = activity;
        mMediaFiles.clear();
        if (mediaType == MediaFile.Type.IMAGE) {
            mMediaFiles.addAll(Utils.getAllLocalImages(activity));
        } else if (mediaType == MediaFile.Type.VIDEO) {
            mMediaFiles.addAll(Utils.getAllLocalVideos(activity));
        } else {
            mMediaFiles.addAll(Utils.getAllLocalMediaFiles(activity));
        }

        Iterator<MediaFile> iterator = mMediaFiles.iterator();
        while (iterator.hasNext()) {
            MediaFile file = iterator.next();
            if (file.getSize() < minFileSize) {
                iterator.remove();
            }
            if (file.getSize() > maxFileSize) {
                iterator.remove();
            }
        }
        Collections.sort(mMediaFiles, (o1, o2) -> (int) (o2.getLastModified() - o1.getLastModified()));

        if (enableCompress) {
            if (mCompressFactory == null) {
                mCompressFactory = new DefaultCompressFactory();
            }
        }
    }

    int selectedCount() {
        int count = 0;
        for (MediaFile mediaFile : mMediaFiles) {
            if (mediaFile.isSelected()) {
                count++;
            }
        }
        return count;
    }

    List<MediaFile> getMediaFiles() {
        return mMediaFiles;
    }

    void finish(OnSelectedListener listener) {
        if (listener != null && mMediaFiles != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                List<String> selectFiles = new ArrayList<>();
                for (MediaFile file : mMediaFiles) {
                    if (file.isSelected()) {
                        if (file.isCompress()) {
                            try {
                                file.setCompressPath(mCompressFactory.compress(mActivity, file.getPath()));
                            } catch (Exception e) {
                                file.setCompressPath(file.getPath());
                            }
                        }
                        selectFiles.add(file.isCompress() ? file.getCompressPath() : file.getPath());
                    }
                }
                listener.onSelected(selectFiles);
                reset();
            });
        }
    }

    MediaFile.Type getMediaType() {
        return mediaType;
    }

    void setMediaType(MediaFile.Type mediaType) {
        this.mediaType = mediaType;
    }

    boolean isEnableCamera() {
        return enableCamera;
    }

    void setEnableCamera(boolean enableCamera) {
        this.enableCamera = enableCamera;
    }

    boolean isEnableEdit() {
        return enableEdit;
    }

    void setEnableEdit(boolean enableEdit) {
        this.enableEdit = enableEdit;
    }

    boolean isEnableCompress() {
        return enableCompress;
    }

    void setEnableCompress(boolean enableCompress) {
        this.enableCompress = enableCompress;
    }

    int getMaxSelectCount() {
        return maxSelectCount;
    }

    void setMaxSelectCount(int maxSelectCount) {
        this.maxSelectCount = maxSelectCount;
    }

    void setMinFileSize(int minFileSize) {
        this.minFileSize = minFileSize;
    }

    void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    void setCompressFactory(CompressFactory compressFactory) {
        this.mCompressFactory = compressFactory;
    }

    void reset() {
        mediaType = null;
        enableCamera = false;
        enableEdit = true;
        enableCompress = true;
        maxSelectCount = 9;
        minFileSize = 1024;
        maxFileSize = 10485760;
        mMediaFiles.clear();
    }
}
