package io.kotest.engine.spec.execution.enabled

import io.kotest.common.reflection.bestName
import io.kotest.core.Logger
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.descriptor
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.extensions.filter.DescriptorFilter
import io.kotest.engine.extensions.filter.DescriptorFilterResult

/**
 * Evaluates a spec against any registered [DescriptorFilter]s.
 */
internal class DescriptorFilterSpecRefEnabledExtension(
   private val projectConfigResolver: ProjectConfigResolver,
) : SpecRefEnabledExtension {

   private val logger = Logger(DescriptorFilterSpecRefEnabledExtension::class)

   override fun isEnabled(ref: SpecRef): EnabledOrDisabled {

      val filters = projectConfigResolver.extensions().filterIsInstance<DescriptorFilter>()
      logger.log { Pair(ref.kclass.bestName(), "${filters.size} descriptor filters") }

      val excluded = filters.firstNotNullOfOrNull {
         val result = it.filter(ref.descriptor())
         result as? DescriptorFilterResult.Exclude
      }

      return if (excluded == null) {
         EnabledOrDisabled.Enabled
      } else {
         EnabledOrDisabled.Disabled(excluded.reason ?: "Disabled by descriptor filter")
      }
   }
}
