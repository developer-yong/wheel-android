package dev.yong.wheel.http;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;

public class BodyRequester extends Requester {

    private final StringBuilder mBuilder;
    private MediaType mMediaType;
    private JSONObject mJSONBody;

    public BodyRequester(String url, RequestMethod method) {
        super(url, method);
        mBuilder = new StringBuilder();
    }

    public BodyRequester setMediaType(MediaType mediaType) {
        this.mMediaType = mediaType;
        return this;
    }

    public BodyRequester add(@NonNull String name, @NonNull String... value) {
        for (String v : value) {
            if (mBuilder.indexOf("=") > 0) {
                mBuilder.append("&");
            }
            mBuilder.append(name).append("=").append(v);
        }
        return this;
    }

    public BodyRequester add(@NonNull Map<String, String> params) {
        for (String name : params.keySet()) {
            String v = params.get(name);
            if (v != null) {
                if (mBuilder.indexOf("=") > 0) {
                    mBuilder.append("&");
                }
                mBuilder.append(name).append("=").append(v);
            }
        }
        return this;
    }

    public BodyRequester addEncoded(@NonNull String name, @NonNull String... value) {
        for (String v : value) {
            if (mBuilder.indexOf("=") > 0) {
                mBuilder.append("&");
            }
            mBuilder.append(Uri.encode(name)).append("=").append(Uri.encode(v));
        }
        return this;
    }

    public BodyRequester addEncoded(@NonNull Map<String, String> params) {
        for (String name : params.keySet()) {
            String v = params.get(name);
            if (v != null) {
                if (mBuilder.indexOf("=") > 0) {
                    mBuilder.append("&");
                }
                mBuilder.append(Uri.encode(name)).append("=").append(Uri.encode(v));
            }
        }
        return this;
    }

    /**
     * 当 MediaType 为 application/json 是使用此方法设置 Json 内容
     * <p>
     * 此方法调用时将替换所有已添加的参数
     *
     * @param json 字符串内容
     * @return BodyRequester
     */
    public BodyRequester json(String json) {
        this.mMediaType = MediaType.get("application/json; charset=utf-8");
        try {
            this.mJSONBody = new JSONObject(json);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    @Override
    public void execute(okhttp3.Callback callback) {
        Map<String, String> globalParams = Transfer.globalParams();
        if (globalParams != null) {
            add(globalParams);
        }
        if (mMediaType == null) {
            mMediaType = Transfer.mediaType();
        }
        String body;
        if (mMediaType != null && mMediaType.toString().contains("application/json")) {
            if (mJSONBody == null) {
                mJSONBody = new JSONObject();
            }
            if (mBuilder.length() > 0) {
                for (String p : mBuilder.toString().split("&")) {
                    if (!TextUtils.isEmpty(p)) {
                        String[] param = p.split("=");
                        if (param.length > 1 && !TextUtils.isEmpty(param[1])) {
                            String name = param[0];
                            String value = param[1];
                            try {
                                if (mJSONBody.has(name)) {
                                    Object v = mJSONBody.get(name);
                                    if (v instanceof JSONArray) {
                                        ((JSONArray) v).put(value);
                                        mJSONBody.put(name, v);
                                    } else {
                                        mJSONBody.remove(name);
                                        JSONArray array = new JSONArray();
                                        array.put(v);
                                        array.put(value);
                                        mJSONBody.put(name, array);
                                    }
                                } else {
                                    mJSONBody.put(name, value);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            body = mJSONBody.toString();
        } else {
            body = mBuilder.toString();
        }
        body(body, mMediaType);
        super.execute(callback);
    }
}
