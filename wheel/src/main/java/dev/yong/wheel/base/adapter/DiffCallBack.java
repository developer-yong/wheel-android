package dev.yong.wheel.base.adapter;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

/**
 * @author CoderYong
 */
public class DiffCallBack<T> extends DiffUtil.Callback {

    private List<T> mOldData;
    private List<T> mNewData;
    private CompareListener<T> mCompareListener;

    DiffCallBack(List<T> oldData, List<T> newData, CompareListener<T> listener) {
        this.mOldData = oldData;
        this.mNewData = newData;
        this.mCompareListener = listener;
    }

    @Override
    public int getOldListSize() {
        return mOldData != null ? mOldData.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewData != null ? mNewData.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mCompareListener.compareItem(mOldData.get(oldItemPosition), mNewData.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return areItemsTheSame(oldItemPosition, newItemPosition) &&
                mCompareListener.compareContent(mOldData.get(oldItemPosition), mNewData.get(newItemPosition));
    }

    public interface CompareListener<T> {

        /**
         * 比较两个对象
         *
         * @param oldObject 旧的对象
         * @param newObject 新的对象
         * @return true为相同
         */
        boolean compareItem(T oldObject, T newObject);

        default boolean compareContent(T oldObject, T newObject) {
            return compareItem(oldObject, newObject);
        }
    }
}
