package io.kotest.engine.js

/**
 * Invoked to set the [KotestFrameworkAdapter] which is used to intercept javascript test calls
 * so that we can re-route them to kotest.
 */
actual fun configureRuntime() {
   setAdapter(KotestFrameworkAdapter)
}

