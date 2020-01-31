package io.kotest.assertions

fun sysprop(key: String, default: String): String = sysprop(key) ?: default

expect fun sysprop(name: String): String?
