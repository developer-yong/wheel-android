package dev.yong.sample.modules.weather;

import java.util.List;

import javax.inject.Inject;

import dev.yong.sample.data.Weather;
import dev.yong.sample.data.WeatherInfo;
import dev.yong.wheel.base.mvp.BasePresenter;
import dev.yong.wheel.base.mvp.IModel;
import dev.yong.wheel.utils.Logger;

/**
 * @author coderyong
 */
public class WeatherPresenter extends BasePresenter<WeatherContract.View, WeatherModel> {

    @Inject
    public WeatherPresenter(WeatherModel model) {
        super(model);
    }

    @Override
    public void takeView(WeatherContract.View view) {
        super.takeView(view);
        loadWeatherList();
    }

    public void loadWeatherList() {
        mModel.loadWeatherList("auto_ip", new IModel.ModelCallBack<List<WeatherInfo>>() {
            @Override
            public void onSuccess(List<WeatherInfo> weathers) {
                mView.showWeatherList(weathers);
            }

            @Override
            public void onFail(String errorMessage) {
                mView.showErrorMessage(errorMessage);
            }
        });
    }

}
