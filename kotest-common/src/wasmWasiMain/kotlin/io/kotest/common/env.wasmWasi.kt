package io.kotest.common

actual fun env(name: String): String? = envmap[name]

private val envmap by lazy { environGet() }


