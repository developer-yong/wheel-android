package dev.yong.wheel.http.retrofit;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @author coderyong
 */
public interface RequestService {
    @GET
    Call<ResponseBody> get(@Url String url);

    @GET
    Call<ResponseBody> get(@Url String url, @QueryMap Map<String, String> map);

    @POST
    Call<ResponseBody> post(@Url String url);

    @POST
    Call<ResponseBody> post(@Url String url, @QueryMap Map<String, String> map);
}
