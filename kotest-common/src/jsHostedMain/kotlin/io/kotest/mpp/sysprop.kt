package io.kotest.mpp

actual fun sysprop(name: String): String? = null
actual fun env(name: String): String? = try {
   jsProcessEnv(name)
} catch (e: Throwable) {
   null
}

private fun jsProcessEnv(name: String): String? = js("process.env[name]")
// For Wasm compatibility, js() invocations must be a single expression within a top-level function.
