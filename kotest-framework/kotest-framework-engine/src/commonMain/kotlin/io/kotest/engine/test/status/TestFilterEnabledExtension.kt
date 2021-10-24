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
      val excluded = filters.map { it.filter(testCase.descriptor) }.firstOrNull { it is TestFilterResult.Exclude }

      return if (filters.isEmpty() || excluded == null) {
         Enabled.enabled
      } else {
         val reason = "${testCase.descriptor.path(false).value} is excluded by test filter: $excluded"
         Enabled.disabled(reason)
            .also { it.reason?.let { log { it } } }
      }
   }
}
