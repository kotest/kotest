package io.kotest.core

import io.kotest.fp.Try
import java.lang.reflect.Method

private val stackWalkerMethod: Method? = try {
   Class.forName("io.kotest.framework.jdk9.SourceRefStackWalker")
      .getMethod("sourceRef")
} catch (t: Throwable) {
   null
}

/**
 * On the JVM we can create a stack trace to get the line number.
 */
actual fun sourceRef(): SourceRef {
   // if we have the jdk9 classes we should use the stack walker - performance !
   return fromStackWalker() ?: fromThreadStackTrace()
}

private fun fromStackWalker() = Try { stackWalkerMethod?.invoke(null) as SourceRef }.getOrNull()

private fun fromThreadStackTrace(): SourceRef {
   val stack = Thread.currentThread().stackTrace
   return stack.dropWhile {
      it.className.startsWith("io.kotest") || it.className.startsWith("java.lang") || it.className.startsWith("com.sun")
   }.first().let { SourceRef(it.lineNumber, it.fileName ?: "unknown") }
}
