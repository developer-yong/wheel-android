package dev.yong.photo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import dev.yong.photo.R;
import dev.yong.photo.bean.Directory;

/**
 * @author coderyong
 */
public class DirectoryAdapter extends BaseAdapter {

    private List<Directory> mDirectories;
    private LayoutInflater mInflater;

    private OnDirectorySelectedListener mListener;

    public DirectoryAdapter(List<Directory> directories, Context context) {
        this.mDirectories = directories;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDirectories == null ? 0 : mDirectories.size();
    }

    @Override
    public Object getItem(int position) {
        return mDirectories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_directory, parent, false);
            holder.ivPic = convertView.findViewById(R.id.iv_directory_pic);
            holder.tvName = convertView.findViewById(R.id.tv_directory_name);
            holder.tvNum = convertView.findViewById(R.id.tv_directory_num);
            holder.rbSelect = convertView.findViewById(R.id.rb_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Directory directory = mDirectories.get(position);
        if (!TextUtils.isEmpty(directory.getPic())) {
            Glide.with(convertView.getContext()).load(directory.getPic()).into(holder.ivPic);
        }
        holder.tvName.setText(directory.getName());
        holder.tvNum.setText(String.format(Locale.getDefault(), "%d张", directory.getMediaFiles().size()));
        holder.rbSelect.setChecked(directory.isSelected());
        holder.rbSelect.setVisibility(
                holder.rbSelect.isChecked() ? View.VISIBLE : View.INVISIBLE);
        convertView.setOnClickListener(v -> {
            for (int i = 0; i < mDirectories.size(); i++) {
                mDirectories.get(i).setSelected(i == position);
            }
            notifyDataSetChanged();
            if (mListener != null) {
                mListener.onSelected(mDirectories.get(position));
            }
        });
        return convertView;
    }

    public void setOnDirectorySelectedListener(OnDirectorySelectedListener listener) {
        mListener = listener;
    }

    public interface OnDirectorySelectedListener {

        /**
         * 选中文件目录时调用
         *
         * @param directory 选中的文件目录
         */
        void onSelected(Directory directory);
    }

    class ViewHolder {
        ImageView ivPic;
        TextView tvName;
        TextView tvNum;
        RadioButton rbSelect;
    }
}
