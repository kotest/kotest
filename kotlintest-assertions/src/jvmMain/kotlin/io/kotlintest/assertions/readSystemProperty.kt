package io.kotlintest.assertions

actual fun readSystemProperty(key: String): String? = System.getProperty(key)
