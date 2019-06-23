package dev.yong.sample.modules.weather;

import android.support.annotation.NonNull;

import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import javax.inject.Inject;

import dev.yong.sample.data.WeatherInfo;
import dev.yong.wheel.base.BaseRecyclerFragment;
import dev.yong.wheel.utils.SnackUtils;

/**
 * @author coderyong
 */
public class WeatherFragment extends BaseRecyclerFragment<WeatherContract.View, WeatherPresenter> implements WeatherContract.View {

    WeatherAdapter mAdapter;

    @Inject
    public WeatherFragment() {
    }

    @Override
    protected void init() {
        super.init();
        mRefreshLayout.setEnableLoadMore(false);
        mRecyclerView.setAdapter(mAdapter);
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
    protected WeatherContract.View provideVew() {
        return this;
    }

    @Override
    protected WeatherPresenter providePresenter() {
        return new WeatherPresenter();
    }
}
