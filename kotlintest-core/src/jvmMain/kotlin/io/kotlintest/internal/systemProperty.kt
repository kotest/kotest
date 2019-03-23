package io.kotlintest.internal

actual fun systemProperty(key: String): String? = System.getProperty(key)