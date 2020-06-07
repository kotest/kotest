package io.kotest.assertions.eq

/**
 * Determine if the object's class is is a data class.
 */
actual fun Any?.isDataClass(): Boolean = this != null && this::class.isData
