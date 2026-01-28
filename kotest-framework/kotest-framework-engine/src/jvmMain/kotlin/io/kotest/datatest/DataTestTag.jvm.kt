package io.kotest.datatest

import io.kotest.core.spec.Spec
import kotlin.reflect.full.isSubclassOf

/**
 * JVM implementation that gets the line number from the stack trace.
 * Looks for the first frame that is inside a Spec subclass or a nested class within a Spec.
 * Highly (ok fully) inspired from [io.kotest.core.source.sourceRef]
 */
internal actual fun getDataTestCallSiteLineNumber(): String {
   val stack = Thread.currentThread().stackTrace

   val frame = stack.firstOrNull { element ->
      runCatching {
         val clazz = Class.forName(element.className)
         isSpecOrNestedInSpec(clazz)
      }.getOrDefault(false)
   }

   return frame?.lineNumber?.takeIf { it > 0 }?.toString() ?: "unknown"
}

/**
 * Checks if the given class is a Spec subclass or is nested inside a Spec subclass.
 * This handles lambdas defined inside specs, which are compiled as nested classes
 * but are not themselves subclasses of Spec.
 */
private fun isSpecOrNestedInSpec(clazz: Class<*>): Boolean {
   // Direct check: is this class a Spec?
   if (runCatching { clazz.kotlin.isSubclassOf(Spec::class) }.getOrDefault(false)) {
      return true
   }

   // Check enclosing classes (for lambdas and nested classes within a Spec)
   var enclosing = clazz.enclosingClass
   while (enclosing != null) {
      if (runCatching { enclosing.kotlin.isSubclassOf(Spec::class) }.getOrDefault(false)) {
         return true
      }
      enclosing = enclosing.enclosingClass
   }

   return false
}

