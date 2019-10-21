package dev.yong.sample.modules.weather;

import dev.yong.sample.data.WeatherInfo;
import dev.yong.wheel.base.mvp.IModel;
import dev.yong.wheel.base.mvp.ListView;

/**
 * @author coderyong
 */
public interface WeatherContract {

    interface View extends ListView<WeatherInfo> {
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
