package io.kotest.mpp

actual fun timeInMillis(): Long = jsDateNow().toLong()

private fun jsDateNow(): Double = js("Date.now()")
// For Wasm compatibility, js() invocations must be a single expression within a top-level function.
