package dev.yong.sample.modules.weather;

import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import dev.yong.sample.data.WeatherInfo;
import dev.yong.wheel.base.BaseRecyclerFragment;
import dev.yong.wheel.base.adapter.BaseRvAdapter;
import dev.yong.wheel.base.mvp.BaseMvpRecyclerFragment;
import dev.yong.wheel.utils.ToastUtils;

/**
 * @author coderyong
 */
public class WeatherFragment extends BaseMvpRecyclerFragment<WeatherInfo, WeatherContract.View, WeatherPresenter> implements WeatherContract.View, OnRefreshListener {

    @Override
    protected void init() {
        super.init();
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setEnableLoadMore(false);

        mPresenter.loadWeatherList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mPresenter.loadWeatherList();
    }

    @Override
    public void showList(boolean isRefresh, List<WeatherInfo> infos) {
        if (isRefresh) {
            mAdapter.replaceData(infos);
        } else {
            mAdapter.addData(infos);
        }
        mRefreshLayout.finishRefresh();
    }

    @Override
    public void showMessage(String message) {
        ToastUtils.show(message);
    }

    @Override
    protected BaseRvAdapter<WeatherInfo> provideAdapter() {
        return new WeatherAdapter();
    }

    @Override
    protected WeatherContract.View provideView() {
        return this;
    }

    @Override
    protected WeatherPresenter providePresenter() {
        return new WeatherPresenter();
    }
}
