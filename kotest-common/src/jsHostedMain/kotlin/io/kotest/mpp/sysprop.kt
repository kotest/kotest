package io.kotest.mpp

actual fun sysprop(name: String): String? = null
actual fun env(name: String): String? = try {
   jsProcessEnv(name)
} catch (_: Throwable) {
   null
}

internal expect fun jsProcessEnv(name: String): String?
