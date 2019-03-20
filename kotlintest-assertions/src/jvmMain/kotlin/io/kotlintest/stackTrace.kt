package io.kotlintest

actual fun stackTrace(t: Throwable): String? {
  return (t.cause ?: t).stackTrace?.firstOrNull {
    !it.className.startsWith("io.kotlintest")
  }?.toString()
}