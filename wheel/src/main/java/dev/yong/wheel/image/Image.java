package dev.yong.wheel.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import dev.yong.wheel.ThreadExecutor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import dev.yong.wheel.cache.DiskLruCache;

/**
 * @author coderyong
 */
public class Image {

    /**
     * 图片获取超时时间
     */
    public static int GET_TIMEOUT = 5000;

    /**
     * 异步获取网络图片
     *
     * @param url 图片地址
     */
    public static void syncGet(String url, ImageView image) {
        if (image == null) {
            return;
        }
        new SyncImage(url, new SimpleCallback(image)).get();
    }

    /**
     * 异步获取网络图片
     *
     * @param url 图片地址
     */
    public static void syncGet(String url, Callback callback) {
        new SyncImage(url, callback).get();
    }

    /**
     * 获取网络图片
     *
     * @param url 图片地址
     * @return Bitmap
     */
    public static Drawable get(String url) {
        Drawable image = null;
        try {
            // 获得连接
            HttpURLConnection conn = getConnection(url);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();//获得图片的数据流
                image = decodeStream(url, is);//读取图像数据
                is.close();
            }
            conn.disconnect();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 获取 HttpURLConnection
     *
     * @param imageUrl 图片地址
     */
    private static HttpURLConnection getConnection(String imageUrl) throws Exception {
        HttpURLConnection conn;
        URL url = new URL(imageUrl);
        if (imageUrl.toLowerCase().startsWith("https")) {
            // https
            conn = (HttpsURLConnection) url.openConnection();
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }
        conn.setConnectTimeout(GET_TIMEOUT);
        conn.setReadTimeout(GET_TIMEOUT);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setUseCaches(false);//不缓存
        conn.setInstanceFollowRedirects(false);
        final int responseCode = conn.getResponseCode();
        // 302, 301 重定向
        if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
            return getConnection(conn.getHeaderField("location"));
        }
        return conn;
    }

    /**
     * 部分手机直接从网络流读取webp图片会黑一半，因此需要转成byte数组来读
     */
    public static Drawable decodeStream(String url, InputStream input) {
        Drawable drawable = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int ch;
            while ((ch = input.read(buff)) > -1) {
                out.write(buff, 0, ch);
            }
            out.flush();
            byte[] data = out.toByteArray();
            out.close();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                drawable = ImageDecoder.decodeDrawable(
                        ImageDecoder.createSource(ByteBuffer.wrap(data)));
                if (drawable instanceof AnimatedImageDrawable) {
                    ((AnimatedImageDrawable) drawable).start();
                }
            } else {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (bitmap != null) {
                    //压缩图片
                    drawable = new BitmapDrawable(Resources.getSystem(), compressImage(bitmap));
                }
            }
            //缓存文件
            String cacheDir = DiskLruCache.getInstance().getCacheDir();
            File file = new File(cacheDir, getFileName(url));
            if (!file.exists() && file.createNewFile()) {
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.write(data);
                raf.close();
            }
            DiskLruCache.getInstance().put(url, file);
        } catch (IOException ignored) {
        }
        return drawable;
    }

    private static String getFileName(String url) {
        try {
            String[] slash = Uri.parse(url).getPath().split("/");
            return slash[slash.length - 1];
        } catch (Exception e) {
            return UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
        }
    }

    /**
     * 质量压缩方法
     *
     * @param image Bitmap
     */
    public static Bitmap compressImage(Bitmap image) {
        if (image == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到out中
        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        int options = 90;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (out.toByteArray().length / 1024 > 1024) {
            out.reset();
            // 这里压缩options%，把压缩后的数据存放到out中
            image.compress(Bitmap.CompressFormat.JPEG, options, out);
            options -= 10;// 每次都减少10
        }
        // 把压缩后的数据out存放到ByteArrayInputStream中
        ByteArrayInputStream is = new ByteArrayInputStream(out.toByteArray());
        return BitmapFactory.decodeStream(is, null, null);
    }

    /**
     * 高斯模糊
     *
     * @param c      Context
     * @param bitmap Bitmap
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurBitmap(Context c, Bitmap bitmap) {
        if (c == null || bitmap == null) {
            return null;
        }
        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap blurBitmap = Bitmap.createBitmap(
                bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(c.getApplicationContext());
        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, blurBitmap);
        //Set the radius of the blur
        blurScript.setRadius(25.f);
        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(blurBitmap);
        //recycle the original bitmap
        bitmap.recycle();
        //After finishing everything, we destroy the Renderscript.
        rs.destroy();
        return blurBitmap;
    }

    static class SyncImage {

        private final String mUrl;
        private final Callback mCallback;
        private Drawable mImage;

        public SyncImage(String url, Callback callback) {
            this.mUrl = url;
            this.mCallback = callback;
        }

        public void get() {
            if (mCallback == null) {
                return;
            }
            if (TextUtils.isEmpty(mUrl)) {
                mCallback.onReady(null);
            }
            File file = DiskLruCache.getInstance().get(mUrl);
            if (file != null) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        mImage = ImageDecoder.decodeDrawable(ImageDecoder.createSource(file));
                        if (mImage instanceof AnimatedImageDrawable) {
                            ((AnimatedImageDrawable) mImage).start();
                        }
                    } else {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        if (bitmap != null) {
                            //压缩图片
                            mImage = new BitmapDrawable(Resources.getSystem(), compressImage(bitmap));
                        }
                    }
                    if (mImage != null) {
                        mCallback.onReady(mImage);
                    }
                    return;
                } catch (Exception ignored) {
                }
            }
            ThreadExecutor.getInstance().execute(() -> {
                //获取并压缩
                mImage = Image.get(mUrl);
                new Handler(Looper.getMainLooper()).post(() -> mCallback.onReady(mImage));
            });
        }
    }

    public static class SimpleCallback implements Callback {

        public ImageView mImage;

        public SimpleCallback(ImageView image) {
            this.mImage = image;
        }

        @Override
        public void onReady(Drawable drawable) {
            if (drawable != null && mImage != null) {
                mImage.setVisibility(View.VISIBLE);
                mImage.setImageDrawable(drawable);
                mImage = null;
            }
        }
    }

    public interface Callback {

        void onReady(Drawable drawable);
    }
}
