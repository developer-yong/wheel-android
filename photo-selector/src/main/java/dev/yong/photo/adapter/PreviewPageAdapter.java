package dev.yong.photo.adapter;

import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

import dev.yong.photo.R;
import dev.yong.photo.bean.MediaFile;

/**
 * @author coderyong
 */
public class PreviewPageAdapter extends PagerAdapter {

    private MediaController mMediaController;
    private MediaPlayer mMediaPlayer;
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
            final VideoView videoView = view.findViewById(R.id.video);
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
                if (!videoView.isPlaying()) {
                    videoView.setVideoPath(path);
                    videoView.start();
                    photoView.setVisibility(View.GONE);
                    v.setVisibility(View.GONE);
                }
            });
            if (mMediaController != null) {
                videoView.setMediaController(mMediaController);
            }
            videoView.setOnCompletionListener(mp -> {
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    mMediaPlayer = mp;
                }
                if (!mp.isPlaying()) {
                    photoView.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                }
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

    public void setMediaController(MediaController controller) {
        mMediaController = controller;
    }

    public void stopVideo() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}