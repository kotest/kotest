package io.kotest.core.source

import io.kotest.common.sysprop
import io.kotest.core.spec.Spec
import io.kotest.engine.config.KotestEngineProperties

private val specJavaClass: Class<*> = Spec::class.java

internal data class JvmStackFrame(
   val declaringClass: Class<*>?,
   val lineNumber: Int,
)

internal expect fun findFirstStackFrame(
   excludeDataTest: Boolean,
   predicate: (JvmStackFrame) -> Boolean,
): JvmStackFrame?

/**
 * On the JVM we can create a stack trace to get the line number.
 * Users can disable the source ref via the system property [KotestEngineProperties.DISABLE_SOURCE_REF].
 */
internal actual fun sourceRef(): SourceRef {
   if (sysprop(KotestEngineProperties.DISABLE_SOURCE_REF, "false") == "true") return SourceRef.None

   val frame = findFirstStackFrame(excludeDataTest = true) { true } ?: return SourceRef.None

   // preference is given to the class name, but we must try to find the enclosing spec
   var kclass: Class<*>? = frame.declaringClass
   try {
      while (kclass != null && !specJavaClass.isAssignableFrom(kclass)) {
         kclass = kclass.enclosingClass
      }
   } catch (_: LinkageError) {
      return SourceRef.None
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
