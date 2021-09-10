package io.kotest.engine.test.status

import io.kotest.core.config.configuration
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.mpp.log

/**
 * This [TestEnabledExtension] disables tests if they are filtered by a [TestFilter].
 */
internal object TestFilterEnabledExtension : TestEnabledExtension {

   override fun isEnabled(testCase: TestCase): Enabled {

      val filters = configuration.filters().filterIsInstance<TestFilter>()
      val includedByFilters = filters.all {
         it.filter(testCase.description) == TestFilterResult.Include
      }
      if (!includedByFilters) {
         return Enabled.disabled("${testCase.description.testPath()} is excluded by test case filters (${filters.size} filters found)")
            .also { log { it.reason } }
      }

      return Enabled.enabled
   }
}
