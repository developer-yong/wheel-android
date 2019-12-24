package dev.yong.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dev.yong.photo.bean.Directory;
import dev.yong.photo.bean.MediaFile;
import dev.yong.photo.compress.CompressFactory;
import dev.yong.photo.compress.DefaultCompressFactory;
import dev.yong.photo.view.ProgressDialog;

/**
 * @author coderyong
 */
public class PhotoSelector {

    private boolean enableCamera = false;
    private boolean enableCrop = false;
    private boolean isCompress = false;
    private MediaFile.Type type;
    private int maxSelectCount = 9;
    private int minFileSize = 1024;
    private int maxFileSize = 10485760;

    private Activity mActivity;
    private OnSelectCountListener mSelectCountListener;
    private OnCompleteListener mCompleteListener;
    private CompressFactory mCompressFactory;

    private List<MediaFile> mMediaFiles;
    private List<MediaFile> mSelectMediaFiles = new ArrayList<>();
    private SparseArray<MediaFile> mMediaFileMap = new SparseArray<>();

    private boolean isFinish = false;

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
        setFinish(false);
        if (activity == null) {
            throw new IllegalArgumentException("Activity must be not null");
        }
        mActivity = activity;
        if (type == MediaFile.Type.IMAGE) {
            mMediaFiles = Utils.getAllLocalImages(mActivity);
        } else if (type == MediaFile.Type.VIDEO) {
            mMediaFiles = Utils.getAllLocalVideos(mActivity);
        } else {
            mMediaFiles = Utils.getAllLocalMediaFiles(mActivity);
        }
        if (mMediaFiles != null) {
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
        }
        mCompleteListener = listener;
        mActivity.startActivity(new Intent(mActivity, SelectorActivity.class));
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
     * 配置压缩工厂实现
     *
     * @param compressFactory 压缩工厂实现类
     */
    public PhotoSelector configCompressFactory(CompressFactory compressFactory) {
        this.mCompressFactory = compressFactory;
        return this;
    }

    /**
     * 配置是否压缩
     *
     * @param isCompress 是否压缩
     */
    public PhotoSelector configCompress(boolean isCompress) {
        if (isCompress && mCompressFactory == null) {
            mCompressFactory = new DefaultCompressFactory();
        }
        this.isCompress = isSupportCompress() && isCompress;
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
     * 配置最小字节数
     *
     * @param byteSize 默认1024B
     */
    public PhotoSelector configMinByte(int byteSize) {
        this.minFileSize = byteSize;
        return this;
    }

    /**
     * 配置最大字节数
     *
     * @param byteSize 默认10485760B
     */
    public PhotoSelector configMaxByte(int byteSize) {
        this.maxFileSize = byteSize;
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

    void onSelectConfirm() {
        if (mCompleteListener != null) {
            if (isSupportCompress() && isCompress) {
                ProgressDialog dialog = new ProgressDialog(mActivity);
                dialog.show("正在压缩...");
                mCompressFactory.compress(mActivity, mSelectMediaFiles, (selectPaths) -> {
                    dialog.dismiss();
                    mCompleteListener.onComplete(selectPaths);
                    reset();
                });
            } else {
                List<String> selectFiles = new ArrayList<>();
                if (mSelectMediaFiles != null) {
                    for (MediaFile file : mSelectMediaFiles) {
                        if (file.isSelected()) {
                            selectFiles.add(file.getPath());
                        }
                    }
                }
                mCompleteListener.onComplete(selectFiles);
                reset();
            }
        }
        setFinish(true);
    }

    void reset() {
        enableCamera = false;
        enableCrop = false;
        isCompress = false;
        maxSelectCount = 9;
        type = null;
        mActivity = null;
        mSelectCountListener = null;
        mCompleteListener = null;
        mCompressFactory = null;
        mMediaFiles = null;
        mSelectMediaFiles.clear();
        mMediaFileMap.clear();
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
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

    boolean isSupportCompress() {
        return mCompressFactory != null;
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
        if (mMediaFiles != null && !mMediaFiles.isEmpty()) {
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
        if (mSelectMediaFiles.size() > 0 && mSelectMediaFiles.get(0).getType() != mediaFile.getType()) {
            Toast.makeText(mActivity, "图片和视频不能同时选择", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mSelectMediaFiles.size() > 0 && mSelectMediaFiles.get(0).getType() == MediaFile.Type.VIDEO) {
            Toast.makeText(mActivity, "您最多只能选择一个视频", Toast.LENGTH_SHORT).show();
            return false;
        }
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
         * @param selectPaths 用户所选择的文件集合
         */
        void onComplete(List<String> selectPaths);
    }
}
