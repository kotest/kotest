package io.kotlintest

actual fun readSystemProperty(key: String): String? = System.getProperty(key)
