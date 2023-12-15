package io.kotest.mpp

actual fun sysprop(name: String): String? = null
actual fun env(name: String): String? = try {
   jsProcessEnv(name)
} catch (e: Throwable) {
   null
}

expect internal fun jsProcessEnv(name: String): String?
