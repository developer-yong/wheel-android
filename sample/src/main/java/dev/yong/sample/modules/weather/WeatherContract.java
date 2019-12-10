package dev.yong.sample.modules.weather;

import java.util.List;

import dev.yong.sample.data.WeatherInfo;
import dev.yong.wheel.base.mvp.IModel;

/**
 * @author coderyong
 */
public interface WeatherContract {

    interface View {
        void showMessage(String message);

        void showList(boolean isRefresh, List<WeatherInfo> infos);
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
