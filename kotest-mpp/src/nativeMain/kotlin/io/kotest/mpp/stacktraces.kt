package io.kotest.mpp

/**
 * Returns the location of this stack trace, removing traces of io.kotest if possible.
 * On some platforms the stack trace may not be available.
 */
actual fun Throwable.throwableLocation(): String? {
   return (cause ?: this).getStackTrace().firstOrNull {
      !it.startsWith("io.kotest")
   }?.toString()
}
