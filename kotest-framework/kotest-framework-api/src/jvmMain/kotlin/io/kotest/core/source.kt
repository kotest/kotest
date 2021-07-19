package io.kotest.core

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.plan.Source
import io.kotest.mpp.sysprop

/**
 * On the JVM we can create a stack trace to get the line number.
 * Users can disable the source ref via the system property [KotestEngineProperties.disableSourceRef].
 */
actual fun source(): Source? {
   if (sysprop(KotestEngineProperties.disableSourceRef, "false") == "true") return null

   val stack = Thread.currentThread().stackTrace
   val firstFrame = stack.dropWhile {
      it.className.startsWith("io.kotest") || it.className.startsWith("java.lang") || it.className.startsWith("com.sun")
   }.firstOrNull() ?: return null

   val fileName = firstFrame.fileName ?: return null
   return Source.FileAndLineSource(fileName, firstFrame.lineNumber)
}
