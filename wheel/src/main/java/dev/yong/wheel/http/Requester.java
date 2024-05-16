package dev.yong.wheel.http;


import android.util.Log;

import androidx.annotation.NonNull;

import dev.yong.wheel.utils.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Requester {

    private final HttpUrl.Builder mUBuilder;
    private final Request.Builder mBuilder;
    private final RequestMethod mMethod;
    private RequestBody mBody;

    public Requester(@NotNull String url, @NotNull RequestMethod method) {
        mUBuilder = HttpUrl.get(url).newBuilder();
        mBuilder = new Request.Builder();
        mMethod = method;
    }

    public Requester removeHeader(@NotNull String name) {
        mBuilder.removeHeader(name);
        return this;
    }

    public Requester addHeader(@NotNull String name, @NotNull String value) {
        mBuilder.addHeader(name, value);
        return this;
    }

    public Requester setHeader(@NotNull String name, @NotNull String value) {
        mBuilder.header(name, value);
        return this;
    }

    public Requester addQuery(@NotNull String name, @NotNull String... value) {
        for (String v : value) {
            mUBuilder.addQueryParameter(name, v);
        }
        return this;
    }

    public Requester setQuery(@NotNull String name, @NotNull String... value) {
        for (String v : value) {
            mUBuilder.setQueryParameter(name, v);
        }
        return this;
    }

    public Requester addEncodedQuery(@NotNull String name, @NotNull String... value) {
        for (String v : value) {
            mUBuilder.addEncodedQueryParameter(name, v);
        }
        return this;
    }

    public Requester setEncodedQuery(@NotNull String name, @NotNull String... value) {
        for (String v : value) {
            mUBuilder.setEncodedQueryParameter(name, v);
        }
        return this;
    }

    public Requester body(@NotNull String body, @Nullable MediaType mediaType) {
        if (mediaType == null) {
            mediaType = Transfer.mediaType();
        }
        mBody = RequestBody.create(body, mediaType);
        return this;
    }

    public Requester body(@NotNull RequestBody body) {
        mBody = body;
        return this;
    }

    public Requester tag(@NotNull Object tag) {
        mBuilder.tag(tag);
        return this;
    }

    public void execute(okhttp3.Callback callback) {
        if (callback == null) {
            callback = new okhttp3.Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    Logger.d(call.request().url().toString(), response.isSuccessful() + "");
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Logger.d(call.request().url().toString(), Log.getStackTraceString(e));
                }
            };
        }
        Map<String, String> globalParams = Transfer.globalParams();
        if (globalParams != null && !mMethod.allowBody()) {
            for (String name : globalParams.keySet()) {
                String value = globalParams.get(name);
                if (value != null) {
                    addQuery(name, value);
                }
            }
        }
        Transfer.client().newCall(
                mBuilder.url(mUBuilder.build())
                        .method(mMethod.name(), mBody)
                        .build()
        ).enqueue(callback);
    }
}
