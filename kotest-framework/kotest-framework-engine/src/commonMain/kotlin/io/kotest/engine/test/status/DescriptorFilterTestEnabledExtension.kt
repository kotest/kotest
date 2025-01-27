package io.kotest.engine.test.status

import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.extensions.DescriptorFilter
import io.kotest.engine.extensions.DescriptorFilterResult

/**
 * This [DescriptorFilterTestEnabledExtension] disables tests if they are filtered by a [DescriptorFilter].
 */
internal class DescriptorFilterTestEnabledExtension(
   private val projectConfigResolver: ProjectConfigResolver,
) : TestEnabledExtension {

   override fun isEnabled(testCase: TestCase): Enabled {

      val filters = projectConfigResolver.extensions().filterIsInstance<DescriptorFilter>()
      val excluded = filters
         .map { it.filter(testCase.descriptor) }
         .filterIsInstance<DescriptorFilterResult.Exclude>()
         .firstOrNull()

      return when {
         excluded == null -> Enabled.enabled
         excluded.reason == null -> Enabled.disabled("${testCase.descriptor.path().value} is excluded by filter(s)")
         else -> Enabled.disabled(excluded.reason)
      }
   }
}
