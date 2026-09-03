package io.kotest.datatest

import io.kotest.core.source.SourceRefUtils
import io.kotest.core.spec.Spec

private val specJavaClass: Class<*> = Spec::class.java

// RETAIN_CLASS_REFERENCE gives us the live java.lang.Class for each frame directly,
// avoiding a Class.forName lookup, and StackWalker.walk() lets us stop as soon as we
// find the first user frame instead of always materializing the whole call stack
// the way Thread.currentThread().stackTrace would do.
private val stackWalker: StackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)

/**
 * JVM implementation that gets the line number from the stack trace.
 * Looks for the first frame that is inside a Spec subclass or a nested class within a Spec.
 * Highly (ok fully) inspired from [io.kotest.core.source.sourceRef]
 */
internal actual fun getDataTestCallSiteLineNumber(): String {
   val frame = stackWalker.walk { frames ->
      frames
         .filter { !SourceRefUtils.isExcludedFrame(it.className, excludeDataTest = false) }
         .filter { isSpecOrNestedInSpec(it.declaringClass) }
         .findFirst()
   }.orElse(null)

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

