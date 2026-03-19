package io.kotest.engine.spec.execution.enabled

import io.kotest.common.platform
import io.kotest.common.reflection.annotation
import io.kotest.core.annotation.RequiresPlatform
import io.kotest.core.spec.SpecRef

/**
 * A [SpecRefEnabledExtension] which will mark a spec as disabled if they are annotated with @[RequiresPlatform]
 * and the engine is not executing on that platform.
 */
internal object RequiresPlatformAnnotationSpecRefEnabledExtension : SpecRefEnabledExtension {

   override fun isEnabled(ref: SpecRef): EnabledOrDisabled {
      val annotation = ref.kclass.annotation<RequiresPlatform>()
         ?: return EnabledOrDisabled.Enabled
      return if (annotation.values.contains(platform))
         EnabledOrDisabled.Enabled
      else
         EnabledOrDisabled.Disabled("Requires platform ${annotation.values.joinToString(",")}")
   }
}
