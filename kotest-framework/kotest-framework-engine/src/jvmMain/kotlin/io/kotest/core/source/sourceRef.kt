package io.kotest.core.source

import io.kotest.core.spec.Spec
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.common.sysprop
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * On the JVM we can create a stack trace to get the line number.
 * Users can disable the source ref via the system property [KotestEngineProperties.DISABLE_SOURCE_REF].
 */
internal actual fun sourceRef(): SourceRef {
   if (sysprop(KotestEngineProperties.DISABLE_SOURCE_REF, "false") == "true") return SourceRef.None

   val stack = Thread.currentThread().stackTrace
   if (stack.isEmpty()) return SourceRef.None

   val frame = stack.dropWhile {
      it.className.startsWith("java.") ||
         it.className.startsWith("javax.") ||
         it.className.startsWith("jdk.internal.") ||
         it.className.startsWith("com.sun") ||
         it.className.startsWith("kotlin.") ||
         it.className.startsWith("kotlinx.") ||
         it.className.startsWith("io.kotest.core.") ||
         it.className.startsWith("io.kotest.engine.")
   }.firstOrNull()

   // preference is given to the class name but we must try to find the enclosing spec
   val kclass = frame?.className?.let { fqn ->
      runCatching {
         var temp: KClass<*>? = Class.forName(fqn).kotlin
         while (temp != null && !temp.isSubclassOf(Spec::class)) {
            temp = temp.java.enclosingClass?.kotlin
         }
         temp
      }.getOrNull()
   }

   val lineNumber = frame?.lineNumber?.takeIf { it > 0 }

   return when {
      kclass == null -> SourceRef.None
      lineNumber == null -> SourceRef.ClassSource(kclass.java.name)
      else -> SourceRef.ClassLineSource(kclass.java.name, lineNumber)
   }
}
