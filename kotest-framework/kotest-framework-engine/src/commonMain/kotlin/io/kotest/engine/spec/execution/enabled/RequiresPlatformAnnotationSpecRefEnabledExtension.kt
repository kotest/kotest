package io.kotest.engine.spec.execution.enabled

import io.kotest.common.JVMOnly
import io.kotest.common.platform
import io.kotest.common.reflection.annotation
import io.kotest.core.annotation.RequiresPlatform
import io.kotest.core.spec.SpecRef

/**
 * A [SpecRefEnabledExtension] which will mark a spec as disabled if they are annotated with @[RequiresPlatform]
 * and the engine is not executing on that platform.
 */
@JVMOnly
internal object RequiresPlatformAnnotationSpecRefEnabledExtension : SpecRefEnabledExtension {

   override fun isEnabled(ref: SpecRef): EnabledOrDisabled {
      return when (val annotation = ref.kclass.annotation<RequiresPlatform>()) {
         null -> EnabledOrDisabled.Enabled
         else -> if (annotation.values.contains(platform))
            EnabledOrDisabled.Enabled
         else
            EnabledOrDisabled.Disabled("Requires platform ${annotation.values.joinToString(",")}")
      }
   }
}
