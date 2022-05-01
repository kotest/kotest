package io.kotest.mpp

actual fun sysprop(name: String): String? = null
actual fun env(name: String): String? = js("process.env[name]") as String?
