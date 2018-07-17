package dev.yong.wheel.http;

/**
 * 网络配置
 *
 * @author coderyong
 */
public interface HttpConfig {
    /**
     * 连接超时
     */
    int CONNECT_TIMEOUT = 20;
    /**
     * 读取超时
     */
    int READ_TIMEOUT = 20;
    /**
     * 写入超时
     */
    int WRITE_TIMEOUT = 20;

    /**
     * 是否错误重连
     */
    boolean ERROR_RECONNECTION = true;

    /**
     * url正则
     */
    String URL_REGEX = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- \\./?%&#=]*)?";
}
