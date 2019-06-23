package dev.yong.wheel.base.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dev.yong.wheel.AppManager;

/**
 * @author CoderYong
 */
public abstract class BaseRvAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = BaseRvAdapter.class.getSimpleName();

    protected Context mContext;

    protected List<T> mList;

    private SparseArray<View> mSpecialItems = new SparseArray<>();

    private OnItemClickListener mListener;

    private boolean isVertical = true;

    /**
     * 子类必须实现该方法，返回列表Item布局资源Id
     *
     * @param parent   {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
     * @param viewType {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
     * @return int
     */
    @LayoutRes
    protected abstract int layoutResId(@NonNull ViewGroup parent, int viewType);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutResId = layoutResId(parent, viewType);
        if (layoutResId == 0) {
            throw new Resources.NotFoundException("Not found layout resources, resources id: " + layoutResId);
        }
        View view = mSpecialItems.get(viewType, View.inflate(parent.getContext(), layoutResId, null));
        if (isVertical) {
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mSpecialItems.size() > 0) {
            if (position == 0) {
                if (mSpecialItems.get(position - 1) != null) {
                    return;
                }
            } else {
                int diff = 0;
                for (int i = -1; i < position; i++) {
                    View view = mSpecialItems.get(i);
                    if (view != null) {
                        diff++;
                    }
                }
                position -= diff;
            }
        }
        onBindView(holder, position);
        if (mListener != null) {
            final int item = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(item);
                }
            });
        }
    }

    /**
     * 绑定条目
     *
     * @param holder   view持有者
     * @param position 条目位置
     */
    public abstract void onBindView(ViewHolder holder, int position);

    @Override
    public int getItemViewType(int position) {
        if (mSpecialItems.size() > 0) {
            if (position == 0 && mSpecialItems.get(position - 1) != null) {
                return -1;
            }
        }
        return mSpecialItems.get(position) == null ? super.getItemViewType(position) : position;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mList != null) {
            count = mList.size();
        }
        return count + mSpecialItems.size();
    }

    /**
     * 添加特殊ItemView
     *
     * @param position item位置
     * @param resId    资源id
     */
    public void addView(int position, int resId) {
        addView(position, View.inflate(AppManager.getInstance().getApplication(), resId, null));
    }

    /**
     * 添加特殊ItemView
     *
     * @param position item位置
     * @param view     布局view
     */
    public void addView(int position, View view) {
        if (position == 0) {
            position = -1;
        }
        mSpecialItems.put(position, view);
    }

    /**
     * 添加头View
     *
     * @param resId 资源id
     */
    public void addHeaderView(int resId) {
        addView(-1, resId);
    }

    /**
     * 添加头View
     *
     * @param view 布局view
     */
    public void addHeaderView(View view) {
        addView(-1, view);
    }

    /**
     * 添加尾View
     *
     * @param resId 资源id
     */
    public void addFooterView(int resId) {
        addView(mList.size(), resId);
    }

    /**
     * 添加尾View
     *
     * @param view 布局view
     */
    public void addFooterView(View view) {
        addView(mList.size(), view);
    }

    public void addData(List<T> list) {
        if (list != null) {
            if (mList == null) {
                mList = list;
            } else {
                this.mList.addAll(list);
            }
            notifyDataSetChanged();
        }
    }

    public void replaceData(List<T> list) {
        if (mList == null) {
            mList = list;
        } else if (list != mList) {
            this.mList.clear();
            this.mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mList;
    }

    public void compareNotify(List<T> newList, DiffCallBack.CompareListener<T> listener) {
        DiffCallBack<T> callBack = new DiffCallBack<>(mList, newList, listener);
        DiffUtil.DiffResult diffResult =
                DiffUtil.calculateDiff(callBack, true);
        replaceData(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }

    public interface OnItemClickListener {
        /**
         * 去除特殊Item的点击事件
         *
         * @param position 点击的Item
         */
        void onItemClick(int position);
    }
}
