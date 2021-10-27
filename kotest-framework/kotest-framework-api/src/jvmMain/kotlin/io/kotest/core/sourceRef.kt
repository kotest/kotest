package io.kotest.core

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.mpp.sysprop

/**
 * On the JVM we can create a stack trace to get the line number.
 * Users can disable the source ref via the system property [KotestEngineProperties.disableSourceRef].
 */
actual fun sourceRef(): SourceRef {
   if (sysprop(KotestEngineProperties.disableSourceRef, "false") == "true") return SourceRef.None

   val stack = Thread.currentThread().stackTrace
   if (stack.isEmpty()) return SourceRef.None

   val frame = stack.dropWhile {
      it.className.startsWith("io.kotest") || it.className.startsWith("java.lang") || it.className.startsWith("com.sun")
   }.firstOrNull()

   val fileName = frame?.fileName
   val className = frame?.className

   return when {
      frame == null -> SourceRef.None
      className != null && frame.lineNumber < 0 -> SourceRef.ClassSource(className)
      className != null -> SourceRef.ClassLineSource(className, frame.lineNumber)
      fileName != null && frame.lineNumber < 0 -> SourceRef.FileLineSource(fileName, frame.lineNumber)
      fileName != null -> SourceRef.None
      else -> SourceRef.None
   }.apply {
      println(this)
   }
}
