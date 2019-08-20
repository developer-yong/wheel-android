package dev.yong.photo.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

import dev.yong.photo.PhotoSelector;
import dev.yong.photo.R;
import dev.yong.photo.bean.MediaFile;

/**
 * @author CoderYong
 */
public class PhotoAdapter extends BaseAdapter {

    private List<MediaFile> mMediaFiles;
    private LayoutInflater mInflater;

    private OnCameraClickListener mOnCameraClickListener;
    private OnItemClickListener mOnItemClickListener;

    private boolean isShowCamera = false;

    public PhotoAdapter(List<MediaFile> mediaFiles, Context context) {
        this.mMediaFiles = mediaFiles;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int count = isShowCamera ? 1 : 0;
        return mMediaFiles == null ? count : count + mMediaFiles.size();
    }

    @Override
    public MediaFile getItem(int position) {
        if (isShowCamera) {
            position--;
        }
        return mMediaFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (isShowCamera) {
            if (position == 0) {
                convertView = mInflater.inflate(R.layout.item_camera, parent, false);
                convertView.setOnClickListener(v -> {
                    if (mOnCameraClickListener != null) {
                        mOnCameraClickListener.onCameraClick();
                    }
                });
                return convertView;
            }
            position--;
        }
        if (convertView == null || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_photo, parent, false);
            holder.ivPhoto = convertView.findViewById(R.id.iv_photo);
            holder.ivVideo = convertView.findViewById(R.id.iv_video);
            holder.tvDuration = convertView.findViewById(R.id.tv_duration);
            holder.cbPhoto = convertView.findViewById(R.id.cb_photo);
            holder.viewBlack = convertView.findViewById(R.id.view_black);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MediaFile mediaFile = mMediaFiles.get(position);
        //设置图片选中状态
        holder.cbPhoto.setChecked(mediaFile.isSelected());
        holder.viewBlack.setVisibility(mediaFile.isSelected() ? View.VISIBLE : View.INVISIBLE);
        if (mediaFile.getType() == MediaFile.Type.VIDEO) {
            holder.ivVideo.setVisibility(View.VISIBLE);
            holder.tvDuration.setVisibility(View.VISIBLE);
            holder.tvDuration.setText(mediaFile.getDuration());
        } else {
            holder.ivVideo.setVisibility(View.GONE);
            holder.tvDuration.setVisibility(View.GONE);
        }
        Glide.with(parent.getContext()).load(mediaFile.getPath()).into(holder.ivPhoto);
        final int item = position;
        holder.cbPhoto.setOnClickListener(v -> {
            MediaFile mediaFile1 = mMediaFiles.get(item);
            boolean isSuccess;
            if (holder.cbPhoto.isChecked()) {
                isSuccess = PhotoSelector.getInstance().addSelected(mediaFile1);
                if (!isSuccess) {
                    holder.cbPhoto.setChecked(false);
                }
            } else {
                isSuccess = PhotoSelector.getInstance().removeSelected(mediaFile1);
            }
            if (isSuccess) {
                zoom(holder.ivPhoto, mediaFile1.isSelected());
                holder.viewBlack.setVisibility(mediaFile1.isSelected() ? View.VISIBLE : View.INVISIBLE);
            }
        });

        if (mOnItemClickListener != null) {
            holder.ivPhoto.setOnClickListener(v -> mOnItemClickListener.onItemClick(item));
        }
        return convertView;
    }

    private void zoom(ImageView imageView, boolean isChecked) {
        AnimatorSet set = new AnimatorSet();
        if (isChecked) {
            set.playTogether(
                    ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.12f),
                    ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.12f)
            );
        } else {
            set.playTogether(
                    ObjectAnimator.ofFloat(imageView, "scaleX", 1.12f, 1f),
                    ObjectAnimator.ofFloat(imageView, "scaleY", 1.12f, 1f)
            );
        }
        set.setDuration(500);
        set.start();
    }

    public void addData(MediaFile... mediaFiles) {
        if (mediaFiles != null) {
            if (mMediaFiles == null) {
                mMediaFiles = Arrays.asList(mediaFiles);
            } else {
                this.mMediaFiles.addAll(Arrays.asList(mediaFiles));
            }
            notifyDataSetChanged();
        }
    }

    public void replaceData(List<MediaFile> mediaFiles) {
        if (mMediaFiles == null) {
            mMediaFiles = mediaFiles;
        } else if (mediaFiles != mMediaFiles) {
            this.mMediaFiles.clear();
            this.mMediaFiles.addAll(mediaFiles);
        }
        notifyDataSetChanged();
    }

    public void setShowCamera(boolean showCamera) {
        isShowCamera = showCamera;
    }

    public void setOnCameraClickListener(OnCameraClickListener listener) {
        this.mOnCameraClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnCameraClickListener {

        /**
         * 相机点击回调
         */
        void onCameraClick();
    }

    public interface OnItemClickListener {

        /**
         * 相册点击回调
         *
         * @param position 相册位置
         */
        void onItemClick(int position);
    }

    public interface OnItemCheckedChangeListener {

        /**
         * 相册选择回调
         *
         * @param mediaFile 选择的文件
         * @param isChecked 是否选中
         */
        void onCheckedChanged(MediaFile mediaFile, boolean isChecked);
    }

    class ViewHolder {
        ImageView ivPhoto;
        ImageView ivVideo;
        TextView tvDuration;
        CheckBox cbPhoto;
        View viewBlack;
    }
}
