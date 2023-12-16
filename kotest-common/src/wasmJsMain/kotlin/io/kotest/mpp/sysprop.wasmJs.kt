package io.kotest.mpp

actual fun jsProcessEnv(name: String): String? = js(
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
