package io.kotest.common

actual fun sysprop(name: String): String? = null

actual fun env(name: String): String? = try {
   js("process.env[name]") as String?
} catch (_: Throwable) {
   null
}
