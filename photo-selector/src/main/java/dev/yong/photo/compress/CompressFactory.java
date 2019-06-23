package dev.yong.photo.compress;

import android.content.Context;

import java.util.List;

import dev.yong.photo.bean.MediaFile;
import dev.yong.photo.view.ProgressDialog;

public interface CompressFactory {

    /**
     * 压缩文件
     *
     * @param context        上下文
     * @param mediaFiles     媒体文件集合
     * @param progressDialog 进度弹窗
     * @param listener       完成监听
     */
    void compress(Context context, List<MediaFile> mediaFiles, ProgressDialog progressDialog, CompressCompleteListener listener);
}
