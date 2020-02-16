package io.kotest.core

/**
 * On the JVM we can create a stack trace to get the line number.
 */
actual fun sourceRef(): SourceRef {
   val stack = Throwable().stackTrace
   return stack.dropWhile {
      it.className.startsWith("io.kotest")
   }[0].run { SourceRef(lineNumber, fileName) }
}
