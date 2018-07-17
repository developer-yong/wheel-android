package dev.yong.sample.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author coderyong
 */
public class BaseEntity<T> {

    private int code;
    private String message;
    @SerializedName("HeWeather6")
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
