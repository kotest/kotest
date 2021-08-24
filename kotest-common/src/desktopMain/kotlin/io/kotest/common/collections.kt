package io.kotest.common

actual fun <K, V> concurrentHashMap(): MutableMap<K, V> = mutableMapOf()
