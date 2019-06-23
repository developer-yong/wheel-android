package dev.yong.sample.modules.weather;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import dev.yong.sample.R;
import dev.yong.sample.data.WeatherInfo;
import dev.yong.wheel.base.adapter.BaseMultiItemRvAdapter;
import dev.yong.wheel.base.adapter.ViewHolder;

import static dev.yong.sample.data.WeatherInfo.TYPE_CURR_WEATHER;
import static dev.yong.sample.data.WeatherInfo.TYPE_FUTURE_WEATHER;
import static dev.yong.sample.data.WeatherInfo.TYPE_WEATHER_SUGGEST;

/**
 * @author coderyong
 */
public class WeatherAdapter extends BaseMultiItemRvAdapter<WeatherInfo> {

    WeatherAdapter() {
    }

    @Override
    protected int layoutResId(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_CURR_WEATHER:
                return R.layout.item_temperature;
            case TYPE_WEATHER_SUGGEST:
                return R.layout.item_suggestion;
            case TYPE_FUTURE_WEATHER:
                return R.layout.item_future_weather;
            default:
                return R.layout.item_temperature;
        }
    }

    @Override
    public void onBindView(ViewHolder holder, int position) {
        WeatherInfo info = mList.get(position);
        switch (info.itemType()) {
            case TYPE_CURR_WEATHER:
                holder.text(R.id.tv_curr_tmp).setText(info.getCurrTmp());
                holder.text(R.id.tv_min_tmp).setText(info.getMinTmp());
                holder.text(R.id.tv_max_tmp).setText(info.getMaxTmp());
                holder.image(R.id.img_weather_icon).setImageResource(info.getWeatherIconRes());
                break;
            case TYPE_WEATHER_SUGGEST:
                holder.text(R.id.tv_title).setText(info.getTitle());
                holder.text(R.id.tv_disc).setText(info.getDescribe());
                holder.image(R.id.img_suggestion_icon).setImageResource(info.getWeatherIconRes());
                break;
            case TYPE_FUTURE_WEATHER:
                holder.text(R.id.tv_title).setText(info.getTitle());
                holder.text(R.id.tv_disc).setText(info.getDescribe());
                holder.text(R.id.tv_min_max).setText(String.format("%s~%s", info.getMinTmp(), info.getMaxTmp()));
                holder.image(R.id.img_weather_icon).setImageResource(info.getWeatherIconRes());
                break;
            default:
                break;
        }
    }
}
