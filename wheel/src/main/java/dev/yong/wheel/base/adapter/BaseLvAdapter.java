package dev.yong.wheel.base.adapter;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author CoderYong
 */
public abstract class BaseLvAdapter<T> extends BaseAdapter {

    protected List<T> mList;

    public BaseLvAdapter(List<T> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
