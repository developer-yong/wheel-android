package dev.yong.photo.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.yong.photo.bean.MediaFile;
import dev.yong.photo.view.photoview.PhotoView;

/**
 *
 * @author coderyong
 */
public class PreviewPageAdapter extends PagerAdapter {

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
        PhotoView photoView = new PhotoView(container.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        photoView.setLayoutParams(params);
        photoView.enable();
        String path = mediaFiles.get(position).getPath();
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(container.getContext())
                .load(path)
                .into(photoView);
        container.addView(photoView);
        return photoView;
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
}