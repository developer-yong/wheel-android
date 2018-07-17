package dev.yong.sample.modules.weather;

import com.alibaba.fastjson.JSON;

import java.util.List;

import dev.yong.wheel.http.DefaultResolveFactory;

/**
 * @author coderyong
 */
public class WeatherResolveFactory extends DefaultResolveFactory {

    @Override
    public <T> List<T> createList(String responseBody, Class<T> tClass) {
        try {
            return JSON.parseArray(JSON.parseObject(responseBody).getString("HeWeather6"), tClass);
        } catch (Exception e) {
            return super.createList(responseBody, tClass);
        }
    }
}
