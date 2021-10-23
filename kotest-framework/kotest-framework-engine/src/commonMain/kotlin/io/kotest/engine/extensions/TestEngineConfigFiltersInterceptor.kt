package io.kotest.engine.extensions

import io.kotest.common.KotestInternal
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.SpecFilterResult
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.engine.TestEngineConfig
import io.kotest.mpp.bestName
import io.kotest.mpp.env
import io.kotest.mpp.sysprop
import kotlin.reflect.KClass

private fun Regex.toTestFilter(): TestFilter = object : TestFilter {
   override fun filter(descriptor: Descriptor): TestFilterResult {
      val name = descriptor.path().value
      return if (this@toTestFilter.matches(name)) TestFilterResult.Include else TestFilterResult.Exclude
   }
}

private fun Regex.toSpecFilter(): SpecFilter = object : SpecFilter {
   override fun filter(kclass: KClass<*>): SpecFilterResult {
      val name = kclass.bestName()
      return if (this@toSpecFilter.matches(name)) SpecFilterResult.Include else SpecFilterResult.Exclude
   }
}

private fun readFilterPropertyToRegexes(setting: String) = setting
   .split(",")
   .filter { it.isNotBlank() }
   .map { runCatching { Regex(it) } }
   .mapNotNull { it.getOrNull() }

@KotestInternal
internal fun createTestEngineConfigFiltersProcessor(
   testFiltersSetting: String,
   specFiltersSetting: String
): TestEngineConfigProcessor {
   return object : TestEngineConfigProcessor {
      override fun process(config: TestEngineConfig): TestEngineConfig {
         return config.copy(
            testFilters = readFilterPropertyToRegexes(testFiltersSetting).map { it.toTestFilter() } + config.testFilters,
            specFilters = readFilterPropertyToRegexes(specFiltersSetting).map { it.toSpecFilter() } + config.specFilters,
         )
      }
   }
}

private fun resolveSystemPropertyOrEnvironmentVariable(name: String) = sysprop(name) ?: env(name) ?: ""

@KotestInternal
internal val TestEngineConfigFiltersFromSystemPropertiesAndEnvironmentInterceptor = run {
   val filterTests = resolveSystemPropertyOrEnvironmentVariable(KotestEngineProperties.filterTests)
   val filterSpecs = resolveSystemPropertyOrEnvironmentVariable(KotestEngineProperties.filterSpecs)

   createTestEngineConfigFiltersProcessor(
      testFiltersSetting = filterTests,
      specFiltersSetting = filterSpecs,
   )
}
