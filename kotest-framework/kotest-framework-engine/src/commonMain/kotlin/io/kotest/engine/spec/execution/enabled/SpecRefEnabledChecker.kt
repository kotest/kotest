package io.kotest.engine.spec.execution.enabled

import io.kotest.core.spec.SpecRef
import io.kotest.engine.config.ProjectConfigResolver

/**
 * Checks if a [SpecRef] is enabled or disabled before it is instantiated.
 *
 * This is not an exhaustive list of all possible ways a spec can be disabled, but rather
 * the set of possibilities that can be checked before an instance of the spec is created.
 *
 * This means certain callbacks can be avoided when a spec is not even needed.
 */
class SpecRefEnabledChecker(
   projectConfigResolver: ProjectConfigResolver
) {

   private val extensions = listOf(
      RequiresPlatformAnnotationSpecRefEnabledExtension,
      EnabledIfAnnotationSpecRefEnabledExtension,
      DisabledIfAnnotationSpecRefEnabledExtension,
      IgnoredAnnotationSpecRefEnabledExtension,
      DescriptorFilterSpecRefEnabledExtension(projectConfigResolver),
      EagerlyExcludedByTagsSpecRefEnabledExtension(projectConfigResolver),
      RequiresTagSpecRefEnabledExtension(projectConfigResolver),
   ) + platformSpecRefEnabledExtensions(projectConfigResolver)

   fun isEnabled(spec: SpecRef): EnabledOrDisabled {
      extensions.forEach {
         val result = it.isEnabled(spec)
         if (result is EnabledOrDisabled.Disabled) return result
      }
      return EnabledOrDisabled.Enabled
   }
}

/**
 * An internal extension that is used to determine if a [SpecRef] is enabled or disabled
 * before that ref is instantiated.
 *
 * Any extension can choose to disable a spec - all extensions must return true
 * for a spec to be considered enabled.
 */
internal interface SpecRefEnabledExtension {
   fun isEnabled(ref: SpecRef): EnabledOrDisabled
}

sealed interface EnabledOrDisabled {
   data object Enabled : EnabledOrDisabled
   data class Disabled(val reason: String?) : EnabledOrDisabled
}

/**
 * Returns a list of platform specific [SpecRefEnabledExtension]s such as the class vvisibility check on the JVM
 */
internal expect fun platformSpecRefEnabledExtensions(
   projectConfigResolver: ProjectConfigResolver
): List<SpecRefEnabledExtension>
