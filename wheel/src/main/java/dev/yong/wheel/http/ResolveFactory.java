package dev.yong.wheel.http;

import java.util.List;

/**
 * 抽象解析工厂类
 *
 * @author coderyong
 */
public interface ResolveFactory {

    /**
     * 创建Code信息
     *
     * @param responseBody 请求响应内容
     * @return code信息 <P>如果有code节点取code节点信息如果没有取默认请求code</P>
     */
    int createCode(String responseBody);

    /**
     * 创建响应Message信息
     *
     * @param responseBody 请求响应内容
     * @return message信息 <P>如果有message节点取message节点信息如果没有返回空字符串</P>
     */
    String createMessage(String responseBody);

    /**
     * 创建响应对象
     *
     * @param responseBody 请求响应内容
     * @param tClass       对象类型
     * @return Object对象 <P>如果有data节点取data节点对象如果没有取根节点对象</P>
     */
    <T> T createObject(String responseBody, Class<T> tClass);

    /**
     * 创建响应对象集合
     *
     * @param responseBody 请求响应内容
     * @param tClass       对象类型
     * @return Object对象集合 <P>如果有data节点取data节点对象集合如果没有取根节点对象集合</P>
     */
    <T> List<T> createList(String responseBody, Class<T> tClass);
}
