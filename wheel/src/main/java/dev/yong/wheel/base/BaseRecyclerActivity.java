package dev.yong.wheel.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import dev.yong.wheel.R;
import dev.yong.wheel.base.adapter.BaseRvAdapter;

public abstract class BaseRecyclerActivity<T> extends BaseActivity {

    protected FrameLayout mLayoutContainer;
    protected SmartRefreshLayout mRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected TextView mTvEmpty;

    protected BaseRvAdapter<T> mAdapter;

    @Override
    protected int layoutId() {
        return useSmartRefresh() ? R.layout.layout_refresh_recycler : R.layout.layout_recycler;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        mLayoutContainer = findViewById(R.id.layout_container);
        if (useSmartRefresh()) {
            mRefreshLayout = findViewById(R.id.refresh);
        }
        mRecyclerView = findViewById(R.id.recycler);
        mTvEmpty = findViewById(R.id.tv_empty);
        if (useItemDecoration()) {
            RecyclerView.ItemDecoration itemDecoration = provideItemDecoration();
            Drawable dividerDrawable = provideDividerDrawable();
            if (dividerDrawable != null) {
                if (itemDecoration instanceof DividerItemDecoration) {
                    ((DividerItemDecoration) itemDecoration).setDrawable(dividerDrawable);
                }
            }
            mRecyclerView.addItemDecoration(itemDecoration);
        }
        mRecyclerView.setLayoutManager(provideLayoutManager());
        if (mAdapter == null) {
            mAdapter = provideAdapter();
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 该方法用于创建一个继承至BaseRvAdapter的Adapter
     *
     * @return BaseRvAdapter
     */
    protected abstract BaseRvAdapter<T> provideAdapter();

    /**
     * 刷新列表数据
     *
     * @param tList 列表数据
     */
    protected void refreshData(List<T> tList) {
        mAdapter.replaceData(tList);
        closeMessageDialog();
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
        }
        showEmpty(mAdapter.getItemCount() == 0);
    }

    /**
     * 添加列表数据
     *
     * @param tList 列表数据
     */
    protected void loadMoreData(List<T> tList) {
        mAdapter.addData(tList);
        closeMessageDialog();
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadMore();
        }
        showEmpty(mAdapter.getItemCount() == 0);
    }

    /**
     * 显示空页面
     *
     * @param isEmpty 是否为空
     */
    protected void showEmpty(boolean isEmpty) {
        if (mTvEmpty != null) {
            mTvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 计算分页页码
     *
     * @param pageSize 分页大小
     * @return 下一页的分页页码
     */
    public int calculatePageNumber(float pageSize) {
        //向上取整并加1
        return (int) Math.ceil(mAdapter.getData().size() / pageSize) + 1;
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
     * 创建LayoutManager
     * <P>默认为LinearLayoutManager</P>
     *
     * @return RecyclerView.LayoutManager
     */
    protected RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(this);
    }

    /**
     * Item分割线
     * <P>默认DividerItemDecoration垂直方向</P>
     *
     * @return RecyclerView.ItemDecoration
     */
    protected RecyclerView.ItemDecoration provideItemDecoration() {
        return new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
    }

    /**
     * 创建分割线Drawable
     *
     * @return Drawable
     */
    protected Drawable provideDividerDrawable() {
        return null;
    }

    /**
     * 是否使用ItemDecoration
     *
     * @return 默认为false
     */
    protected boolean useItemDecoration() {
        return false;
    }

}
