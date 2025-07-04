package io.kotest.engine.spec

import java.util.concurrent.ConcurrentHashMap

actual fun <K, V> threadSafeMap(): MutableMap<K, V> = ConcurrentHashMap()
