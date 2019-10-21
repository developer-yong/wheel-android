package dev.yong.wheel.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import dev.yong.wheel.R;
import dev.yong.wheel.base.adapter.BaseRvAdapter;
import dev.yong.wheel.base.mvp.BaseMvpFragment;
import dev.yong.wheel.base.mvp.IPresenter;
import dev.yong.wheel.base.mvp.ListView;
import dev.yong.wheel.utils.ToastUtils;

/**
 * @author coderyong
 */
public abstract class BaseRecyclerFragment<T, V extends ListView<T>, P extends IPresenter<V>>
        extends BaseMvpFragment<V, P> implements OnRefreshLoadMoreListener, ListView<T> {

    protected SmartRefreshLayout mRefreshLayout;
    protected RecyclerView mRecyclerView;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.ItemDecoration mItemDecoration;
    protected Drawable mDivider;

    protected BaseRvAdapter<T> mAdapter;

    private boolean isSupportSwipeBack = false;
    private boolean isLazyLoad = true;
    private boolean isLoaded = false;
    private boolean isViewCreated = false;

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
        isViewCreated = true;
        if (mAdapter == null) {
            mAdapter = provideAdapter();
        }
        mRecyclerView.setAdapter(mAdapter);
        if (isLazyLoad) {
            if (isViewCreated && getUserVisibleHint() && !isLoaded) {
                loadList(true);
                isLoaded = true;
                isViewCreated = true;
            }
        } else {
            loadList(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isLazyLoad && isViewCreated && isVisibleToUser && !isLoaded) {
            loadList(true);
            isLoaded = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLoaded = false;
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager == null ? new LinearLayoutManager(mContext) : mLayoutManager;
    }

    protected RecyclerView.ItemDecoration getDividerItemDecoration() {
        return mItemDecoration == null ? new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL) : mItemDecoration;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadList(false);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        loadList(true);
    }

    /**
     * 该方法用于创建一个继承至BaseRvAdapter的Adapter
     *
     * @return BaseRvAdapter
     */
    protected abstract BaseRvAdapter<T> provideAdapter();

    /**
     * 该方法用于加载列表数据
     *
     * @param isRefresh 是否为下拉刷新
     */
    protected abstract void loadList(boolean isRefresh);

    @Override
    public void showList(boolean isRefresh, List<T> tList) {
        if (isRefresh) {
            mAdapter.replaceData(tList);
        } else {
            mAdapter.addData(tList);
        }
        closeMessageDialog();
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public void showMessage(String message) {
        ToastUtils.show(message);
        closeMessageDialog();
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
            mRefreshLayout.finishLoadMore();
        }
    }

    public void setLazyLoad(boolean lazyLoad) {
        isLazyLoad = lazyLoad;
    }

    public void setSupportSwipeBack(boolean isSupportSwipeBack) {
        this.isSupportSwipeBack = isSupportSwipeBack;
    }

    @Override
    public boolean isSupportSwipeBack() {
        return isSupportSwipeBack;
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
