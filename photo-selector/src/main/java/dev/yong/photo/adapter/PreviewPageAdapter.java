package dev.yong.photo.adapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;

import dev.yong.photo.R;
import dev.yong.photo.bean.MediaFile;

/**
 * @author coderyong
 */
public class PreviewPageAdapter extends PagerAdapter {

    private OnItemClickListener mOnItemClickListener;

    private List<MediaFile> mediaFiles;

    public PreviewPageAdapter(List<MediaFile> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    @Override
    public int getCount() {
        return mediaFiles == null ? 0 : mediaFiles.size();
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        MediaFile file = mediaFiles.get(position);
        if (file.getType() == MediaFile.Type.IMAGE) {
            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setLayoutParams(params);
            String path = file.getPath();
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(container)
                    .load(path)
                    .into(photoView);
            if (mOnItemClickListener != null) {
                photoView.setOnClickListener(v -> mOnItemClickListener.onItemClick(position));
            }
            container.addView(photoView);
            return photoView;
        } else {
            View view = View.inflate(container.getContext(), R.layout.layout_video, null);
            final PhotoView photoView = view.findViewById(R.id.image);
            final ImageButton button = view.findViewById(R.id.btn_play);

            final String path = file.getPath();
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            Glide.with(container)
                    .load(path)
                    .into(photoView);
            if (mOnItemClickListener != null) {
                photoView.setOnClickListener(v -> mOnItemClickListener.onItemClick(position));
            }
            button.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    /*7.0以上要通过FileProvider将File转化为Uri*/
                    uri = FileProvider.getUriForFile(v.getContext(), "dev.yong.photo.fileprovider", new File(path));
                } else {
                    /*7.0以下则直接使用Uri的fromFile方法将File转化为Uri*/
                    uri = Uri.parse(path);
                }
                intent.setDataAndType(uri, "video/*");
                v.getContext().startActivity(intent);
            });
            container.addView(view);
            return view;
        }
    }

    /**
     * 销毁page
     * position： 当前需要消耗第几个page
     * object:当前需要消耗的page
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    /**
     * true: 表示不去创建，使用缓存  false:去重新创建
     * view： 当前滑动的view
     * object：将要进入的新创建的view，由instantiateItem方法创建
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}