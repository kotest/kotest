package io.kotest.core

/**
 * On the JVM we can create a stack trace to get the line number.
 */
actual fun sourceRef(): SourceRef {
   val stack = Thread.currentThread().stackTrace
   return stack.dropWhile {
      it.className.startsWith("io.kotest") || it.className.startsWith("java.lang")
   }.first().let { SourceRef(it.lineNumber, it.fileName ?: "unknown") }
}
