package dev.yong.sample.modules.weather;

import java.util.List;

import dev.yong.sample.data.WeatherInfo;
import dev.yong.wheel.base.mvp.IModel;

/**
 * @author coderyong
 */
public interface WeatherContract {

    interface View {

        /**
         * 显示错误信息
         *
         * @param message 服务器返回信息
         */
        void showErrorMessage(String message);

        /**
         * 展示天气列表
         *
         * @param weatherList 天气列表数据
         */
        void showWeatherList(List<WeatherInfo> weatherList);
    }

    interface Model extends IModel {
        /**
         * 加载天气列表数据
         *
         * @param callBack 数据获取回调
         */
        void loadWeatherList(ModelCallBack<WeatherInfo> callBack);
    }
}
