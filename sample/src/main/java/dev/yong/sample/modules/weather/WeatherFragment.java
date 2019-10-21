package dev.yong.sample.modules.weather;

import dev.yong.sample.data.WeatherInfo;
import dev.yong.wheel.base.BaseRecyclerFragment;
import dev.yong.wheel.base.adapter.BaseRvAdapter;

/**
 * @author coderyong
 */
public class WeatherFragment extends BaseRecyclerFragment<WeatherInfo, WeatherContract.View, WeatherPresenter> implements WeatherContract.View {

    @Override
    protected void loadList(boolean isRefresh) {
        mPresenter.loadWeatherList();
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
