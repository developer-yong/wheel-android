package dev.yong.wheel.http.retrofit;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @author coderyong
 */
public interface UploadService {
    @Multipart
    @POST
    Call<ResponseBody> upload(@Url String url, @Part MultipartBody.Part file);

    @POST
    Call<ResponseBody> upload(@Url String url, @QueryMap Map<String, String> map, @Body MultipartBody multipartBody);

    @POST
    Call<ResponseBody> upload(@Url String url, @Body MultipartBody multipartBody);

    @Multipart
    @POST
    Call<ResponseBody> upload(@Url String url, @Part() List<MultipartBody.Part> parts);

    @Multipart
    @POST
    Call<ResponseBody> upload(@Url String url,
                              @QueryMap Map<String, String> map,
                              @Part() List<MultipartBody.Part> parts);
}
