package dev.yong.wheel.http

import java.lang.reflect.Type

interface ParserFactory {
    fun <T> parser(content: String, type: Type): T
}