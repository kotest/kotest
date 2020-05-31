package io.kotest.mpp

/**
 * Returns the first line of a stack trace that isn't from the io.kotest packages.
 * On some platforms the stack trace may not be available.
 */
expect fun Throwable.throwableLocation(): String?
