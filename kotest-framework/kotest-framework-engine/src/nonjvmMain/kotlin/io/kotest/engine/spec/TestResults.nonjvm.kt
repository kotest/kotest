package io.kotest.engine.spec

actual fun <K, V> threadSafeMap(): MutableMap<K, V> = mutableMapOf()
