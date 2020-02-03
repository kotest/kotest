package io.kotest.mpp

fun sysprop(key: String, default: String): String = sysprop(key) ?: default

expect fun sysprop(name: String): String?

expect fun env(name: String): String?
