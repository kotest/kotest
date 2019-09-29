package io.kotest.core

actual fun sourceRef(): SourceRef {
  val stack = Throwable().stackTrace
  return stack.dropWhile {
    it.className.startsWith("io.kotest")
  }[0].run { SourceRef(lineNumber, fileName) }
}
