package io.kotest.core

actual fun sourceRef(): SourceRef {
   // creates an exception in order to get the stack for the current point
   val stack = Throwable().stackTrace
   return stack.dropWhile {
      it.className.startsWith("io.kotest")
   }[0].run { SourceRef(lineNumber, fileName) }
}
