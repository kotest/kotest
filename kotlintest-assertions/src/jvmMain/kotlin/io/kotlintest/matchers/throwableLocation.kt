package io.kotlintest.matchers

actual fun Throwable.throwableLocation(): String? {
  return (cause ?: this).stackTrace?.firstOrNull {
    !it.className.startsWith("io.kotlintest")
  }?.toString()
}
