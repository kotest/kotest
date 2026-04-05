package io.kotest.engine.spec.execution.enabled

import io.kotest.common.JVMOnly
import io.kotest.common.syspropOrEnv
import io.kotest.core.spec.SpecRef
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.config.ProjectConfigResolver
import kotlin.reflect.KVisibility

/**
 * A [SpecRefEnabledExtension] which will ignore private specs
 * when [io.kotest.engine.config.ProjectConfigResolver.ignorePrivateClasses] returns true.
 *
 * Since visibility modifiers are only available via reflection on the JVM,
 * this is a JVM-only interceptor.
 */
@JVMOnly
internal class ClassVisibilitySpecRefEnabledExtension(
   private val projectConfigResolver: ProjectConfigResolver,
) : SpecRefEnabledExtension {

   override fun isEnabled(ref: SpecRef): EnabledOrDisabled {
      if (syspropOrEnv(KotestEngineProperties.KOTEST_TEST_ENABLED_OVERRIDE) == "true") return EnabledOrDisabled.Enabled
      return when {
         ref is SpecRef.Reference &&
            ref.kclass.visibility == KVisibility.PRIVATE &&
            projectConfigResolver.ignorePrivateClasses() -> EnabledOrDisabled.Disabled("Disabled by ignorePrivateClasses")

         else -> EnabledOrDisabled.Enabled
      }
   }
}
