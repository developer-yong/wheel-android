package dev.yong.sample.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.io.File;

import dev.yong.sample.image.transform.CircleTransform;
import dev.yong.sample.image.transform.RoundTransform;

/**
 * Image加载帮助类
 *
 * @author coderyong
 */
public class Image {

    private static final String TAG = Image.class.getSimpleName();
    private Context mContext;

    private Image() {
    }

    private static class ImageHelperHolder {
        @SuppressLint("StaticFieldLeak")
        private final static Image INSTANCE = new Image();
    }

    public static Image with(Context context) {
        if (context == null) {
            throw new NullPointerException(TAG + " --- Context不能为空！");
        } else {
            ImageHelperHolder.INSTANCE.mContext = context;
            return ImageHelperHolder.INSTANCE;
        }
    }

    /**
     * 加载网络图片
     *
     * @param path 图片路径
     * @return Builder
     */
    public Builder load(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new NullPointerException(TAG + "path不能为空！");
        }
        return load(Uri.parse(path));
    }

    /**
     * 加载本地文件
     *
     * @param file 图片文件
     * @return Builder
     */
    public Builder load(File file) {
        if (file == null) {
            throw new NullPointerException(TAG + "file不能为空！");
        } else if (!file.exists()) {
            throw new NullPointerException(TAG + "文件不存在！");
        }
        return load(Uri.fromFile(file));
    }

    /**
     * 加载本地图片
     *
     * @param uri 图片Uri地址
     * @return Builder
     */
    public Builder load(Uri uri) {
        if (uri == null) {
            throw new NullPointerException(TAG + "uri不能为空！");
        }
        return new Builder(this, uri);
    }

    public static class Builder {

        private Image helper;
        private Uri uri;
        private int targetWidth;
        private int targetHeight;
        private int errorResId;
        private ScaleType scaleType;
        private boolean isCircular = false;
        private int circularSize = 0;
        private Transformation transformation;

        public Builder(Image helper, Uri uri) {
            this.helper = helper;
            this.uri = uri;
        }

        /**
         * 设置图片显示的大小
         *
         * @param targetWidth  图片目标宽
         * @param targetHeight 图片目标高
         * @return Builder
         */
        public Builder resizePx(int targetWidth, int targetHeight) {
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
            return this;
        }

        /**
         * 设置加载错误图片
         *
         * @param resId 图片资源Id
         * @return Builder
         */
        public Builder error(int resId) {
            this.errorResId = resId;
            return this;
        }

        /**
         * 设置图片显示类型
         *
         * @param scaleType 显示模式{@link ScaleType}
         * @return Builder
         */
        public Builder scaleType(ScaleType scaleType) {
            this.scaleType = scaleType;
            return this;
        }

        /**
         * 设置圆图显示
         *
         * @return Builder
         */
        public Builder isCircular() {
            isCircular = true;
            return this;
        }

        /**
         * 设置圆角矩形
         *
         * @return Builder
         */
        public Builder round(int circularSize) {
            this.circularSize = circularSize;
            return this;
        }

        /**
         * 设置圆角矩形
         *
         * @return Builder
         */
        public Builder transform(Transformation transformation) {
            this.transformation = transformation;
            return this;
        }

        /**
         * 显示图片到View
         *
         * @param imageView imageView
         */
        public void into(ImageView imageView) {
            RequestCreator creator = Picasso.get().load(uri);
            if (targetWidth != 0 && targetHeight != 0) {
                creator.resize(targetWidth, targetHeight);
            }
            if (errorResId != 0) {
                creator.error(errorResId);
            }
            switch (scaleType) {
                case CENTER:
                case CENTER_CROP:
                    creator.centerCrop();
                    break;
                case CENTER_INSIDE:
                    creator.centerInside();
                    break;
                case FIT_CENTER:
                case FIT_XY:
                case FIT_START:
                case FIT_END:
                    creator.fit();
                    break;
                default:
                    break;
            }
            if (isCircular) {
                creator.transform(new CircleTransform());
            }
            if (circularSize > 0) {
                creator.transform(new RoundTransform(circularSize));
            }
            if (transformation != null) {
                creator.transform(transformation);
            }
            creator.into(imageView);
        }
    }
}
