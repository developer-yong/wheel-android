package dev.yong.photo.compress;

import android.content.Context;

public interface CompressFactory {

    /**
     * 异步压缩
     *
     * @param context   上下文
     * @param mediaPath 媒体文件路径
     */
    String compress(Context context, String mediaPath) throws Exception;
}
