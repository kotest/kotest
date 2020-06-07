package io.kotest.assertions.eq

/**
 * Determine if class is a data class, not supported on this platform
 */
actual fun Any?.isDataClass(): Boolean = false
