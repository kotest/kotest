package io.kotest.datatest

import io.kotest.core.spec.Spec
import kotlin.reflect.full.isSubclassOf

/**
 * JVM implementation that gets the line number from the stack trace.
 * Looks for the first frame that is inside a Spec subclass.
 * Highly (ok fully) inspired from [io.kotest.core.source.sourceRef]
 */
internal actual fun getDataTestCallSiteLineNumber(): String {
   val stack = Thread.currentThread().stackTrace

   val frame = stack.firstOrNull { element ->
      runCatching {
         val kclass = Class.forName(element.className).kotlin
         kclass.isSubclassOf(Spec::class)
      }.getOrDefault(false)
   }

   return frame?.lineNumber?.takeIf { it > 0 }?.toString() ?: "unknown"
}
