package dev.yong.wheel.http;

import java.lang.reflect.Type;

public interface ParserFactory {

    <T> T parser(String content, Type type);
}
