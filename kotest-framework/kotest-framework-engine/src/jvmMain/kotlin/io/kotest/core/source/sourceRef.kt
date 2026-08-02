package io.kotest.core.source

import io.kotest.common.sysprop
import io.kotest.core.spec.Spec
import io.kotest.engine.config.KotestEngineProperties

private val specJavaClass: Class<*> = Spec::class.java

// RETAIN_CLASS_REFERENCE gives us the live java.lang.Class for each frame directly,
// avoiding a Class.forName lookup, and StackWalker.walk() lets us stop as soon as we
// find the first user frame instead of always materializing the whole call stack
// the way Thread.currentThread().stackTrace would do.
private val stackWalker: StackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)

/**
 * On the JVM we can create a stack trace to get the line number.
 * Users can disable the source ref via the system property [KotestEngineProperties.DISABLE_SOURCE_REF].
 */
internal actual fun sourceRef(): SourceRef {
   if (sysprop(KotestEngineProperties.DISABLE_SOURCE_REF, "false") == "true") return SourceRef.None

   val frame = SourceRefUtils.firstUserFrame(stackWalker) ?: return SourceRef.None

   // preference is given to the class name, but we must try to find the enclosing spec
   var kclass: Class<*>? = frame.declaringClass
   while (kclass != null && !specJavaClass.isAssignableFrom(kclass)) {
      kclass = kclass.enclosingClass
   }

   val lineNumber = frame.lineNumber.takeIf { it > 0 }

   return when {
      kclass == null -> SourceRef.None
      lineNumber == null -> SourceRef.ClassSource(kclass.name)
      else -> SourceRef.ClassLineSource(kclass.name, lineNumber)
   }
}

object SourceRefUtils {

   /**
    * Returns the first user-land frame from the given [StackWalker], walking the live call
    * stack lazily so frames beyond the match are never materialized.
    */
   internal fun firstUserFrame(walker: StackWalker): StackWalker.StackFrame? {
      return walker.walk { frames ->
         frames.filter { !isExcludedFrame(it.className, excludeDataTest = true) }.findFirst()
      }.orElse(null)
   }

   /**
    * Returns the first user-land frame from the given stack trace.
    *
    * That is, we strip all the invocations from JDK, Kotest, internal sun libraries, etc, in an attempt
    * to find the location where the user defined the test.
    */
   internal fun firstUserFrame(stack: Array<StackTraceElement>): StackTraceElement? {
      return filteredUserFrames(stack, excludeDataTest = true).firstOrNull()
   }

   internal fun filteredUserFrames(stack: Array<StackTraceElement>, excludeDataTest: Boolean = false): List<StackTraceElement> {
      return stack.dropWhile { isExcludedFrame(it.className, excludeDataTest) }
   }

   internal fun isExcludedFrame(className: String, excludeDataTest: Boolean): Boolean {
      return className.startsWith("java.") ||
         className.startsWith("javax.") ||
         className.startsWith("jdk.internal.") ||
         className.startsWith("com.sun") ||
         className.startsWith("kotlin.") ||
         className.startsWith("kotlinx.") ||
         className.startsWith("io.kotest.core.") ||
         className.startsWith("io.kotest.engine.") ||
         (excludeDataTest && className.startsWith("io.kotest.datatest."))
   }
}
