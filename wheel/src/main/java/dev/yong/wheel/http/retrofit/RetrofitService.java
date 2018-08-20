package dev.yong.wheel.http.retrofit;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author coderyong
 */
public interface RetrofitService {
    @GET
    Call<ResponseBody> get(@Url String url);

    @GET
    Call<ResponseBody> get(@Url String url, @QueryMap Map<String, String> map);

    @POST
    Call<ResponseBody> post(@Url String url);

    @FormUrlEncoded
    @POST
    Call<ResponseBody> post(@Url String url, @FieldMap Map<String, String> params);

    @POST
    Call<ResponseBody> postBody(@Url String url, @Body RequestBody requestBody);

    @FormUrlEncoded
    @POST
    Call<ResponseBody> postWithHeader(@Url String url, @HeaderMap Map<String, String> headerMap, @FieldMap Map<String, String> params);

    @HEAD
    Call<ResponseBody> head(@Url String url, @QueryMap Map<String, String> params);

    @FormUrlEncoded
    @PUT
    Call<ResponseBody> put(@Url String url, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @PATCH
    Call<ResponseBody> patch(@Url String url, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @DELETE
    Call<ResponseBody> delete(@Url String url, @FieldMap Map<String, String> params);

    @Streaming
    @GET
    Call<ResponseBody> download(@Url String url);

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
