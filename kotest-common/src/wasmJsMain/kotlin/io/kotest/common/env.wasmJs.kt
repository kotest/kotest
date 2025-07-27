package io.kotest.common

actual fun env(name: String): String? = try {
   jsProcessEnv("process.env[name]")
} catch (_: Throwable) {
   null
}


@Suppress("unused")
private fun jsProcessEnv(name: String): String? = js(
   // Work around K/Wasm limitation:
   //     "The Kotlin try-catch expression can't catch JavaScript exceptions."
   //     https://kotlinlang.org/docs/wasm-js-interop.html#exception-handling
   """
        (() => {
          try {
            return process.env[name];
          } catch (e) {
          }
        })() ?? null
    """
)
