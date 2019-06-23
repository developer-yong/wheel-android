package dev.yong.sample.modules.weather;

import java.util.List;

import dev.yong.sample.data.WeatherInfo;
import dev.yong.wheel.base.mvp.IModel;
import dev.yong.wheel.base.mvp.IPresenter;

/**
 * @author coderyong
 */
public class WeatherPresenter implements IPresenter<WeatherContract.View> {

    private WeatherModel mModel;
    private WeatherContract.View mView;

    public WeatherPresenter() {
        mModel = new WeatherModel();
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

    @Override
    public void takeView(WeatherContract.View view) {
        mView = view;
    }

    @Override
    public void dropView() {

    }
}
