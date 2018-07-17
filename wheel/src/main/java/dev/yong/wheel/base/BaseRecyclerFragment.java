package dev.yong.wheel.base;

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
import dev.yong.wheel.base.mvp.IView;

/**
 * @author coderyong
 */
public abstract class BaseRecyclerFragment<V extends IView, P extends IPresenter<V>> extends BaseMvpFragment<V, P> implements OnRefreshLoadMoreListener {

    protected SmartRefreshLayout mRefreshLayout;
    protected RecyclerView mRecyclerView;

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

        RecyclerView.ItemDecoration decoration = getDividerItemDecoration();
        if (decoration != null) {
            mRecyclerView.addItemDecoration(decoration);
        }
        mRecyclerView.setLayoutManager(getLayoutManager());
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(mContext);
    }

    protected RecyclerView.ItemDecoration getDividerItemDecoration() {
        return new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
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
}
