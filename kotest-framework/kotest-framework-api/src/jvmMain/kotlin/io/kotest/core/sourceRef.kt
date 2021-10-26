package io.kotest.core

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.mpp.sysprop

/**
 * On the JVM we can create a stack trace to get the line number.
 * Users can disable the source ref via the system property [KotestEngineProperties.disableSourceRef].
 */
actual fun sourceRef(): SourceRef {
   if (sysprop(KotestEngineProperties.disableSourceRef, "false") == "true") return SourceRef(-1, "unknown")
   val stack = Thread.currentThread().stackTrace
   return stack.dropWhile {
      it.className.startsWith("io.kotest") || it.className.startsWith("java.lang") || it.className.startsWith("com.sun")
   }.first().let { SourceRef(it.lineNumber, it.fileName ?: "unknown") }
}
