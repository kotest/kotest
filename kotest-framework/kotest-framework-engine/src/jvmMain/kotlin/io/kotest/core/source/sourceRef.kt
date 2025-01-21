package io.kotest.core.source

import io.kotest.core.spec.Spec
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.mpp.sysprop
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * On the JVM we can create a stack trace to get the line number.
 * Users can disable the source ref via the system property [KotestEngineProperties.disableSourceRef].
 */
internal actual fun sourceRef(): SourceRef {
   if (sysprop(KotestEngineProperties.disableSourceRef, "false") == "true") return SourceRef.None

   val stack = Thread.currentThread().stackTrace
   if (stack.isEmpty()) return SourceRef.None

   val frame = stack.dropWhile {
      it.className.startsWith("io.kotest") ||
         it.className.startsWith("java.lang") ||
         it.className.startsWith("com.sun") ||
         it.className.startsWith("kotlin.") ||
         it.className.startsWith("kotlinx.")
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

   return when {
      frame == null -> SourceRef.None
      kclass != null -> SourceRef.ClassSource(kclass.java.name, frame.lineNumber.takeIf { it > 0 })
      else -> SourceRef.None
   }
}
