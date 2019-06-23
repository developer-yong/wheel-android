package dev.yong.wheel.base.adapter;

/**
 * @author coderyong
 */
public abstract class BaseMultiItemRvAdapter<T extends BaseMultiItemRvAdapter.ItemType> extends BaseRvAdapter<T> {

    @Override
    public int getItemViewType(int position) {
        if (mList == null) return super.getItemViewType(position);
        ItemType type = mList.get(position);
        return type != null ? type.itemType() : super.getItemViewType(position);
    }

    public interface ItemType {
        /**
         * item类型
         *
         * @return T
         */
        int itemType();
    }
}
