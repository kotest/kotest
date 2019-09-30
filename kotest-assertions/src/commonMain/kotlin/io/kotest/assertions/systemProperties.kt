package io.kotest.assertions

fun readSystemProperty(key: String, default: String): String = readSystemProperty(key) ?: default

expect fun readSystemProperty(key: String): String?
