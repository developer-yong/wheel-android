package dev.yong.photo.compress;

import android.content.Context;
import android.text.TextUtils;

public class DefaultCompressFactory implements CompressFactory {

    public String compress(Context context, String mediaPath) throws Exception {
        return Luban.with(context)
                .ignoreBy(100)
                .setTargetDir(context.getCacheDir().getAbsolutePath())
                .filter(path -> !TextUtils.isEmpty(path)
                        && (path.toLowerCase().endsWith(".png")
                        || path.toLowerCase().endsWith(".jpg")
                        || path.toLowerCase().endsWith(".jpeg")))
                .get(mediaPath).getAbsolutePath();
    }
}
