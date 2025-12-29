package io.kotest.engine.test.status

import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.extensions.DescriptorFilter
import io.kotest.engine.extensions.DescriptorFilterResult
import io.kotest.common.syspropOrEnv

/**
 * Applies test and spec filters using sysprop or env vars from [KotestEngineProperties.FILTER_TESTS]
 * and [KotestEngineProperties.FILTER_SPECS].
 *
 * These work similarly to gradle filters in --tests described at:
 * https://docs.gradle.org/current/userguide/java_testing.html#full_qualified_name_pattern
 */
internal object SystemPropertyTestFilterEnabledExtension : TestEnabledExtension {

   private val logger = Logger(SystemPropertyTestFilterEnabledExtension::class)

   override fun isEnabled(testCase: TestCase): Enabled {

      val filter = syspropOrEnv(KotestEngineProperties.FILTER_TESTS)
      logger.log { Pair(testCase.name.name, "Filter tests syspropOrEnv=$filter") }

      if (filter == null || filter.isBlank()) return Enabled.enabled

      val excluded = filter
         .propertyToRegexes()
         .map { it.toTestFilter().filter(testCase.descriptor) }
         .filterIsInstance<DescriptorFilterResult.Exclude>()
         .firstOrNull()

      logger.log { Pair(testCase.name.name, "excluded = $excluded") }
      return if (excluded == null) Enabled.enabled else Enabled.disabled(excluded.reason)
   }
}

private fun Regex.toTestFilter(): DescriptorFilter = object : DescriptorFilter {
   override fun filter(descriptor: Descriptor): DescriptorFilterResult {
      val name = descriptor.path().value
      return if (this@toTestFilter.matches(name))
         DescriptorFilterResult.Include
      else
         DescriptorFilterResult.Exclude("Excluded by 'kotest.filter.tests': ${this@toTestFilter}")
   }
}

private fun String.propertyToRegexes(): List<Regex> =
   this.split(",")
      .filter { it.isNotBlank() }
      .map { it.replace("*", ".*?").toRegex() }
