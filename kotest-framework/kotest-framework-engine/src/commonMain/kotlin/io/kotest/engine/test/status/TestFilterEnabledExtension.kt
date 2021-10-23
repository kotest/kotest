package io.kotest.engine.test.status

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.mpp.log

/**
 * This [TestEnabledExtension] disables tests if they are filtered by a [TestFilter].
 */
internal class TestFilterEnabledExtension(private val registry: ExtensionRegistry) : TestEnabledExtension {

   override fun isEnabled(testCase: TestCase): Enabled {

      val filters = registry.all().filterIsInstance<TestFilter>()
      val includedByAll = filters.all {
         it.filter(testCase.descriptor) == TestFilterResult.Include
      }
      if (!includedByAll) {
         val reason = "${testCase.descriptor.path()} is excluded by test case filters (${filters.size} filters found)"
         return Enabled.disabled(reason)
            .also { log { it.reason } }
      }
      return Enabled.enabled
   }
}
