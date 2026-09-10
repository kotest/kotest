package io.kotest.datatest

import io.kotest.core.source.findFirstStackFrame
import io.kotest.core.spec.Spec

private val specJavaClass: Class<*> = Spec::class.java

/**
 * JVM implementation that gets the line number from the stack trace.
 * Looks for the first frame that is inside a Spec subclass or a nested class within a Spec.
 * Highly (ok fully) inspired from [io.kotest.core.source.sourceRef]
 */
internal actual fun getDataTestCallSiteLineNumber(): String {
   val frame = findFirstStackFrame(excludeDataTest = false) {
      it.declaringClass?.let(::isSpecOrNestedInSpec) == true
   }

   return frame?.lineNumber?.takeIf { it > 0 }?.toString() ?: "unknown"
}

/**
 * Checks if the given class is a Spec subclass or is nested inside a Spec subclass.
 * This handles lambdas defined inside specs, which are compiled as nested classes
 * but are not themselves subclasses of Spec.
 */
private fun isSpecOrNestedInSpec(clazz: Class<*>): Boolean {
   var current: Class<*>? = clazz
   while (current != null) {
      if (specJavaClass.isAssignableFrom(current)) return true
      current = current.enclosingClass
   }
   return false
}
