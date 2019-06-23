package dev.yong.wheel.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import dev.yong.wheel.R;
import dev.yong.wheel.base.mvp.BaseMvpFragment;
import dev.yong.wheel.base.mvp.IPresenter;

/**
 * @author coderyong
 */
public abstract class BaseRecyclerFragment<V, P extends IPresenter<V>> extends BaseMvpFragment<V, P> implements OnRefreshLoadMoreListener {

    protected SmartRefreshLayout mRefreshLayout;
    protected RecyclerView mRecyclerView;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.ItemDecoration mItemDecoration;
    protected Drawable mDivider;

    @Override
    protected int createLayoutId() {
        return useSmartRefresh() ? R.layout.layout_refresh_recycler : R.layout.layout_recycler;
    }

    @Override
    protected void init(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.init(view, savedInstanceState);
        if (useSmartRefresh()) {
            mRefreshLayout = view.findViewById(R.id.refresh);
            mRefreshLayout.setOnRefreshLoadMoreListener(this);
        }
        mRecyclerView = view.findViewById(R.id.recycler);
        if (useItemDecoration()) {
            RecyclerView.ItemDecoration itemDecoration = getDividerItemDecoration();
            if (mDivider != null) {
                if (itemDecoration instanceof DividerItemDecoration) {
                    ((DividerItemDecoration) itemDecoration).setDrawable(mDivider);
                }
            }
            mRecyclerView.addItemDecoration(itemDecoration);
        }
        mRecyclerView.setLayoutManager(getLayoutManager());
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager == null ? new LinearLayoutManager(mContext) : mLayoutManager;
    }

    protected RecyclerView.ItemDecoration getDividerItemDecoration() {
        return mItemDecoration == null ? new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL) : mItemDecoration;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
    }

    /**
     * 是否使用SmartRefreshLayout
     *
     * @return 默认为true
     */
    protected boolean useSmartRefresh() {
        return true;
    }

    /**
     * 是否使用ItemDecoration
     *
     * @return 默认为false
     */
    protected boolean useItemDecoration() {
        return false;
    }

    /**
     * 设置布局管理器
     *
     * @param layoutManager layoutManager
     */
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    /**
     * 设置分割线
     *
     * @param itemDecoration itemDecoration
     */
    public void setItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        this.mItemDecoration = itemDecoration;
    }

    /**
     * 设置分割线
     *
     * @param divider 分割线
     */
    public void setDividerDrawable(Drawable divider) {
        this.mDivider = divider;
    }
}
