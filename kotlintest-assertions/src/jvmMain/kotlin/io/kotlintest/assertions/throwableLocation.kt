package io.kotlintest.assertions

actual fun Throwable.throwableLocation(): String? {
  return (cause ?: this).stackTrace?.firstOrNull {
    !it.className.startsWith("io.kotlintest")
  }?.toString()
}
