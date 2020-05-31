@file:JvmName("stacktracesjvm")

package io.kotest.mpp

/**
 * Returns the first line of a stack trace that isn't from the io.kotest packages.
 * On some platforms the stack trace may not be available.
 */
actual fun Throwable.throwableLocation(): String? {
   return (cause ?: this).stackTrace?.firstOrNull {
      !it.className.startsWith("io.kotest")
   }?.toString()
}
