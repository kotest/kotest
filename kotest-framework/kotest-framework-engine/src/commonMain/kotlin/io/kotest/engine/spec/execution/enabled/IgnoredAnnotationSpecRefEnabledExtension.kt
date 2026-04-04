package io.kotest.engine.spec.execution.enabled

import io.kotest.common.JVMOnly
import io.kotest.common.reflection.annotation
import io.kotest.core.Logger
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.name

/**
 * Skips any spec marked with the [Ignored] annotation.
 *
 * Note: annotations are only available on the JVM.
 */
@JVMOnly
internal object IgnoredAnnotationSpecRefEnabledExtension : SpecRefEnabledExtension {

   private val logger = Logger(IgnoredAnnotationSpecRefEnabledExtension::class)

   override fun isEnabled(ref: SpecRef): EnabledOrDisabled {
      val annotation = ref.kclass.annotation<Ignored>()
      logger.log { Pair(ref.name(), "@Ignored == $annotation") }
      return if (annotation == null) {
         EnabledOrDisabled.Enabled
      } else {
         val reason = annotation.reason.let {
            if (it.isBlank())
               "Disabled by @Ignored"
            else
               """Disabled by @Ignored(reason="$it")"""
         }
         EnabledOrDisabled.Disabled(reason)
      }
   }
}
