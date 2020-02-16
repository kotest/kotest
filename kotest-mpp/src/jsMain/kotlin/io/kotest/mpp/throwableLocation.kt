package io.kotest.mpp

/**
 * Stack trace is not available on JS.
 */
actual fun Throwable.throwableLocation(): String? = null
