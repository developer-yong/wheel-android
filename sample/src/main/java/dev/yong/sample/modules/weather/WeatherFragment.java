package dev.yong.sample.modules.weather;

import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import dev.yong.sample.data.WeatherInfo;
import dev.yong.wheel.base.BaseRecyclerFragment;
import dev.yong.wheel.utils.SnackUtils;

/**
 * @author coderyong
 */
public class WeatherFragment extends BaseRecyclerFragment<WeatherContract.View, WeatherPresenter> implements WeatherContract.View {

    private WeatherAdapter mAdapter;

    @Override
    protected void init() {
        super.init();
        mAdapter = new WeatherAdapter();
        mRefreshLayout.setEnableLoadMore(false);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.loadWeatherList();
    }

    @Override
    public void showErrorMessage(String message) {
        SnackUtils.show(getView(), message);
    }

    @Override
    public void showWeatherList(List<WeatherInfo> weatherList) {
        mAdapter.replaceData(weatherList);
        mRefreshLayout.finishRefresh();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        super.onRefresh(refreshLayout);
        mPresenter.loadWeatherList();
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
