package dev.yong.wheel.http;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import dev.yong.wheel.utils.Logger;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * @author coderyong
 */
public interface Callback<T> extends retrofit2.Callback<ResponseBody> {

    @Override
    default void onResponse(@NonNull final Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            try {
                ResponseBody responseBody = response.body();
                String body = "";
                if (responseBody != null) {
                    body = responseBody.string();
                }
                onResponse(body);
                onResponse(parse(body));
            } catch (IOException e) {
                onFailure(e);
            }
        } else {
            onFailure(new Exception("request failed, response's code is: " + response.code()));
        }
    }

    @Override
    default void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
        onFailure(t);
    }

    default T parse(String content) {
        Type type = ((ParameterizedType) getClass()
                .getGenericInterfaces()[0]).getActualTypeArguments()[0];
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                    if (src == src.longValue()) {
                        return new JsonPrimitive(src.longValue());
                    }
                    return new JsonPrimitive(src);
                })
                .create();
        try {
            return gson.fromJson(content, type);
        } catch (JsonSyntaxException e) {
            Logger.e(e);
            Map<String, Object> map = gson.fromJson(
                    content, new TypeToken<Map<String, Object>>() {}.getType());
            try {
                Object obj = type.getClass().newInstance();
                Field[] fields = obj.getClass().getFields();
                for (Field field : fields) {
                    int modifiers = field.getModifiers();
                    if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                        continue;
                    }
                    field.setAccessible(true);
                    field.set(obj, map.get(field.getName()));
                }
                return (T) obj;
            } catch (Exception e1) {
                Logger.e(e1);
                return null;
            }
        }
    }

    /**
     * 请求成功
     *
     * @param t 请求成功后得到的响应数据
     */
    void onResponse(T t);


    /**
     * 请求失败
     *
     * @param t 错误信息
     */
    void onFailure(Throwable t);

    /**
     * 请求成功
     *
     * @param responseBody 请求成功后得到的响应数据
     */
    default void onResponse(String responseBody) {
    }
}