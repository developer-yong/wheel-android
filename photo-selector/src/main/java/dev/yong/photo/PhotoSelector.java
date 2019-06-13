package dev.yong.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.yong.photo.bean.Directory;
import dev.yong.photo.bean.MediaFile;
import dev.yong.photo.compress.CompressListener;

/**
 * @author coderyong
 */
public class PhotoSelector {

    private boolean enableCamera = false;
    private boolean enableCrop = false;
    private boolean isCompress = true;
    private MediaFile.Type type;
    private int maxSelectCount = 9;

    private Activity mActivity;
    private OnSelectCountListener mSelectCountListener;
    private CompressListener mCompressListener;
    private OnCompleteListener mCompleteListener;

    private List<MediaFile> mMediaFiles;
    private List<MediaFile> mSelectMediaFiles = new ArrayList<>();
    private SparseArray<MediaFile> mMediaFileMap = new SparseArray<>();

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
    }

    public void select(Activity activity, OnCompleteListener listener) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity must be not null");
        }
        mActivity = activity;
        if (type == MediaFile.Type.IMAGE) {
            mMediaFiles = Utils.getAllLocalImages(activity);
        } else if (type == MediaFile.Type.VIDEO) {
            mMediaFiles = Utils.getAllLocalVideos(activity);
        } else {
            mMediaFiles = Utils.getAllLocalMediaFiles(activity);
        }
        mCompleteListener = listener;
        activity.startActivity(new Intent(activity, SelectorActivity.class));
    }

    /**
     * 配置是否启用相机
     *
     * @param enable 默认false
     */
    public PhotoSelector configCameraEnable(boolean enable) {
        this.enableCamera = enable;
        return this;
    }

    /**
     * 配置是否启用裁剪
     *
     * @param enable 默认false
     */
    public PhotoSelector configCropEnable(boolean enable) {
        this.enableCrop = enable;
        return this;
    }

    /**
     * 配置压缩
     *
     * @param compressListener 压缩监听类
     */
    public PhotoSelector configCompress(CompressListener compressListener) {
        this.mCompressListener = compressListener;
        this.isCompress = compressListener != null;
        return this;
    }

    /**
     * 配置媒体文件类型
     *
     * @param type 默认视频和图片
     */
    public PhotoSelector configMediaType(MediaFile.Type type) {
        this.type = type;
        return this;
    }

    /**
     * 配置最大选择数量
     *
     * @param count 默认9张
     */
    public PhotoSelector configMaxSelectCount(int count) {
        this.maxSelectCount = count;
        return this;
    }

    /**
     * 设置文件父级目录
     *
     * @param parentDir 默认空（所有文件）
     */
    void setParentDir(String parentDir) {
        mMediaFileMap.clear();
        if (!TextUtils.isEmpty(parentDir) && mMediaFiles != null && !mMediaFiles.isEmpty()) {
            int position = 0;
            for (MediaFile file : mMediaFiles) {
                if (parentDir.equals(new File(file.getPath()).getParent())) {
                    mMediaFileMap.put(position, file);
                    position++;
                }
            }
        }
    }

    /**
     * 设置选择数量监听
     *
     * @param listener OnSelectCountListener
     */
    void setOnSelectCountListener(OnSelectCountListener listener) {
        if (listener != null) {
            listener.onCount(mSelectMediaFiles.size());
        }
        this.mSelectCountListener = listener;
    }

    void onSelectConfirm(Context context) {
        if (mCompleteListener != null) {
            if (isCompress) {
                mCompressListener.compress(mActivity, mMediaFiles, mCompleteListener);
            } else {
                List<File> selectFiles = new ArrayList<>();
                if (mMediaFiles != null) {
                    for (MediaFile file : mMediaFiles) {
                        if (file.isSelected()) {
                            selectFiles.add(new File(file.getPath()));
                        }
                    }
                }
                if (context != null && mActivity != null) {
                    Intent intent = new Intent(context, mActivity.getClass());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
                mCompleteListener.onComplete(selectFiles);
            }
        }
        reset();
    }

    void reset() {
        mMediaFiles = null;
        mActivity = null;
        mCompleteListener = null;
        mSelectCountListener = null;
        mSelectMediaFiles.clear();
        mMediaFileMap.clear();
    }

    boolean enableCamera() {
        return enableCamera;
    }

    boolean enableCrop() {
        return enableCrop;
    }

    boolean isCompress() {
        return isCompress;
    }

    MediaFile.Type mediaType() {
        return type;
    }

    int maxSelectCount() {
        return maxSelectCount;
    }

    List<MediaFile> getMediaFiles() {
        return new ArrayList<>(mMediaFiles);
    }

    List<Directory> getDirectories() {
        if (mMediaFiles != null) {
            Map<String, Directory> directoryMap = new HashMap<>(16);
            for (MediaFile file : mMediaFiles) {
                String parent = new File(file.getPath()).getParent();
                Directory directory = directoryMap.get(parent);
                if (directory == null) {
                    directory = new Directory();
                    directory.setPic(file.getPath());
                    directory.setPath(parent);
                    directory.setNumber(directory.getNumber() + 1);
                    directoryMap.put(parent, directory);
                } else {
                    directory.setNumber(directory.getNumber() + 1);
                }
            }
            List<Directory> directories = new ArrayList<>(directoryMap.values());
            Directory directory = new Directory();
            directory.setPic(mMediaFiles.get(0).getPath());
            String name = "图片和视频";
            if (type == MediaFile.Type.IMAGE) {
                name = "所有图片";
            } else if (type == MediaFile.Type.VIDEO) {
                name = "所有视频";
            }
            directory.setName(name);
            directory.setNumber(mMediaFiles.size());
            directory.setSelected(true);
            directories.add(0, directory);
            return directories;
        }
        return null;
    }

    List<MediaFile> getDirMediaFiles() {
        if (mMediaFileMap.size() > 0) {
            List<MediaFile> mediaFiles = new ArrayList<>();
            for (int i = 0; i < mMediaFileMap.size(); i++) {
                mediaFiles.add(mMediaFileMap.valueAt(i));
            }
            return mediaFiles;
        } else {
            return new ArrayList<>(mMediaFiles);
        }
    }

    public boolean addSelected(MediaFile mediaFile) {
        if (mSelectMediaFiles.size() == maxSelectCount) {
            Toast.makeText(mActivity, "您最多只能选择" + maxSelectCount + "张", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mSelectCountListener != null) {
            mSelectCountListener.onCount(mSelectMediaFiles.size() + 1);
        }
        mediaFile.setSelected(true);
        return mSelectMediaFiles.add(mediaFile);
    }

    public boolean removeSelected(MediaFile mediaFile) {
        if (mSelectMediaFiles.size() == 0) {
            return false;
        }
        if (mSelectCountListener != null) {
            mSelectCountListener.onCount(mSelectMediaFiles.size() - 1);
        }
        mediaFile.setSelected(false);
        return mSelectMediaFiles.remove(mediaFile);
    }

    List<MediaFile> getSelectedMediaFiles() {
        return mSelectMediaFiles;
    }

    public interface OnSelectCountListener {
        /**
         * 选择时被调用
         *
         * @param selectCount 用户所选择的数量
         */
        void onCount(int selectCount);
    }

    public interface OnCompleteListener {
        /**
         * 选择确认时被调用
         *
         * @param selectFiles 用户所选择的文件集合
         */
        void onComplete(List<File> selectFiles);
    }
}
