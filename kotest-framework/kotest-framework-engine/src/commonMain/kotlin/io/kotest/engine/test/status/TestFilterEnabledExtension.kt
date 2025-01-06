package io.kotest.engine.test.status

import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase

/**
 * This [TestEnabledExtension] disables tests if they are filtered by a [TestFilter].
 */
internal class TestFilterEnabledExtension(private val registry: ExtensionRegistry) : TestEnabledExtension {

   override fun isEnabled(testCase: TestCase): Enabled {

      val filters = registry.all().filterIsInstance<TestFilter>()
      val excluded = filters
         .map { it.filter(testCase.descriptor) }
         .filterIsInstance<TestFilterResult.Exclude>()
         .firstOrNull()

      return when {
         excluded == null -> Enabled.enabled
         excluded.reason == null -> Enabled.disabled("${testCase.descriptor.path(false).value} is excluded by test filter(s)")
         else -> Enabled.disabled("${testCase.descriptor.path(false).value} is excluded by test filter(s): ${excluded.reason}")
      }
   }
}
