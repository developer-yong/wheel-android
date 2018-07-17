package dev.yong.sample.service;

import java.util.List;

import dev.yong.sample.data.BaseEntity;
import dev.yong.sample.data.Weather;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author coderyong
 */
public interface ApiService {

    String URL_BASE = "https://free-api.heweather.com/s6/";

    String URL_WEATHER = "weather?key=69bcb5a6d326411baccdad8ebaa06cba";

    @GET(URL_WEATHER)
    Observable<BaseEntity<List<Weather>>> getWeatherList(@Query("location") String city);
}
