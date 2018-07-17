package dev.yong.wheel.http;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * 默认解析工厂类
 *
 * @author coderyong
 */
public class DefaultResolveFactory implements ResolveFactory {

    @Override
    public int createCode(String responseBody) {
        int code = 200;
        try {
            code = JSON.parseObject(responseBody).getIntValue("code");
            return code != 0 ? code : 200;
        } catch (Exception e) {
            return code;
        }
    }

    @Override
    public String createMessage(String responseBody) {
        try {
            return JSON.parseObject(responseBody).getString("message");
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public <T> T createObject(String responseBody, Class<T> tClass) {
        try {
            return JSON.parseObject(JSON.parseObject(responseBody).getString("data"), tClass);
        } catch (Exception e) {
            return JSON.parseObject(responseBody, tClass);
        }
    }

    @Override
    public <T> List<T> createList(String responseBody, Class<T> tClass) {
        try {
            return JSON.parseArray(JSON.parseObject(responseBody).getString("data"), tClass);
        } catch (Exception e) {
            return JSON.parseArray(responseBody, tClass);
        }
    }
}
