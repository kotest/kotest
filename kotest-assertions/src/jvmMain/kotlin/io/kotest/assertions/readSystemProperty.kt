package io.kotest.assertions

actual fun readSystemProperty(key: String): String? = System.getProperty(key)
