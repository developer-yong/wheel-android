package dev.yong.wheel.oaid;

/**
 * 统一异常
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
public final class OAIDException extends RuntimeException {

    public OAIDException(String message) {
        super(message);
    }

    public OAIDException(Throwable cause) {
        super(cause);
    }

}
