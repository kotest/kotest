package io.kotest.engine.spec.execution.enabled

import io.kotest.common.JVMOnly
import io.kotest.common.reflection.IncludingAnnotations
import io.kotest.common.reflection.IncludingSuperclasses
import io.kotest.common.reflection.annotation
import io.kotest.common.reflection.instantiations
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.SpecRef

/**
 * Evaluates any spec annotated with [EnabledIf] to check if it should be executed.
 *
 * Note: annotations are only available on the JVM.
 */
@JVMOnly
internal object EnabledIfAnnotationSpecRefEnabledExtension : SpecRefEnabledExtension {

   override fun isEnabled(ref: SpecRef): EnabledOrDisabled {

      val condition = ref.kclass
         .annotation<EnabledIf>(IncludingAnnotations, IncludingSuperclasses)
         ?.condition
         ?.let { instantiations.newInstanceNoArgConstructorOrObjectInstance(it) }

      if (condition == null) return EnabledOrDisabled.Enabled

      val result = condition.evaluate(ref.kclass)
      return if (result) {
         EnabledOrDisabled.Enabled
      } else {
         EnabledOrDisabled.Disabled("Disabled by @EnabledIf (${condition::class.simpleName})")
      }
   }
}
