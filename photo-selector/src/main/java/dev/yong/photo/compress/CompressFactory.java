package dev.yong.photo.compress;

import android.content.Context;

import java.util.List;

import dev.yong.photo.bean.MediaFile;

public interface CompressFactory {

    /**
     * 压缩文件
     *
     * @param context    上下文
     * @param mediaFiles 媒体文件集合
     * @param listener   完成监听
     */
    void compress(Context context, List<MediaFile> mediaFiles, CompressCompleteListener listener);
}