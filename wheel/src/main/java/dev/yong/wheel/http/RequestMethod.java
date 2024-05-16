package dev.yong.wheel.http;

import java.util.Locale;

public enum RequestMethod {

    GET("GET"),

    POST("POST"),

    PUT("PUT"),

    DELETE("DELETE"),

    HEAD("HEAD"),

    PATCH("PATCH"),

    OPTIONS("OPTIONS"),

    TRACE("TRACE");

    private final String value;

    RequestMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean allowBody() {
        switch (this) {
            case POST:
            case PUT:
            case PATCH:
            case DELETE: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public static RequestMethod reverse(String method) {
        method = method.toUpperCase(Locale.ENGLISH);
        switch (method) {
            case "POST": {
                return POST;
            }
            case "PUT": {
                return PUT;
            }
            case "DELETE": {
                return DELETE;
            }
            case "HEAD": {
                return HEAD;
            }
            case "PATCH": {
                return PATCH;
            }
            case "OPTIONS": {
                return OPTIONS;
            }
            case "TRACE": {
                return TRACE;
            }
            case "GET":
            default: {
                return GET;
            }
        }
    }
}
