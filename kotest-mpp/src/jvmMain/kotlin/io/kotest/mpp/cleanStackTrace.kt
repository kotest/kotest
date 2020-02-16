package io.kotest.mpp

/**
 * Returns a stack trace, removing traces of io.kotest if possible, and unwrapping for reflection calls.
 * On some platforms the stack trace may not be available.
 */
actual fun Throwable.throwableLocation(): String? {
   return (cause ?: this).stackTrace?.firstOrNull {
      !it.className.startsWith("io.kotest")
   }?.toString()
}
