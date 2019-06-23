package dev.yong.photo.compress;

import android.content.Context;

import java.io.File;
import java.util.List;

import dev.yong.photo.PhotoSelector;
import dev.yong.photo.bean.MediaFile;

/**
 * @author coderyong
 */
public interface CompressCompleteListener {

    /**
     * 压缩文件
     *
     * @param context     上下文
     * @param selectPaths 文件集合路径集合
     */
    void compress(Context context, List<String> selectPaths);

}
