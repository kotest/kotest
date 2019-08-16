package io.kotlintest.assertions

fun readSystemProperty(key: String, default: String): String = readSystemProperty(key) ?: default

expect fun readSystemProperty(key: String): String?
