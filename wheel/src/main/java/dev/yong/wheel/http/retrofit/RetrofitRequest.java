package dev.yong.wheel.http.retrofit;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * @author CoderYong
 */

public class RetrofitRequest extends RetrofitCall {

    private RequestService mService;
    private String mUrl;
    private Map<String, String> mParameters;

    RetrofitRequest(RequestService service, String url, Map<String, String> parameters) {
        this.mService = service;
        this.mUrl = url;
        this.mParameters = parameters;
    }

    @Override
    protected Call<ResponseBody> getCall() {
        return mParameters != null && mParameters.size() > 0 ?
                mService.get(mUrl, mParameters) : mService.get(mUrl);
    }

    @Override
    protected Call<ResponseBody> postCall() {
        return mParameters != null && mParameters.size() > 0 ?
                mService.post(mUrl, mParameters) : mService.post(mUrl);
    }
}
