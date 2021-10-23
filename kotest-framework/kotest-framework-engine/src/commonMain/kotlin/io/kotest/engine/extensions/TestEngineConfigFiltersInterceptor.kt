package io.kotest.engine.extensions

import io.kotest.common.KotestInternal
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.SpecFilterResult
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.engine.TestEngineConfig
import io.kotest.fp.Try
import io.kotest.mpp.bestName
import io.kotest.mpp.env
import io.kotest.mpp.sysprop
import kotlin.reflect.KClass

private fun Regex.toTestFilter(): TestFilter = object : TestFilter {
   val regex = this@toTestFilter
   override fun filter(descriptor: Descriptor): TestFilterResult {
      val name = descriptor.path().value
      return if (regex.matches(name)) TestFilterResult.Include else TestFilterResult.Exclude
   }
}

private fun Regex.toSpecFilter(): SpecFilter = object : SpecFilter {
   val regex = this@toSpecFilter
   override fun filter(kclass: KClass<*>): SpecFilterResult {
      val name = kclass.bestName()
      return if (regex.matches(name)) SpecFilterResult.Include else SpecFilterResult.Exclude
   }
}

private fun readFilterPropertyToRegexes(setting: String) = setting
   .split(",")
   .filter { it.isNotBlank() }
   .map { Try { Regex(it) } }
   .mapNotNull { it.valueOrNull() }

@OptIn(KotestInternal::class)
internal fun createTestEngineConfigFiltersProcessor(testFiltersSetting: String, specFiltersSetting: String): TestEngineConfigProcessor {
   return object : TestEngineConfigProcessor {
      override fun process(config: TestEngineConfig): TestEngineConfig {
         val result = config.copy(
            testFilters = readFilterPropertyToRegexes(testFiltersSetting).map { it.toTestFilter() } + config.testFilters,
            specFilters = readFilterPropertyToRegexes(specFiltersSetting).map { it.toSpecFilter() } + config.specFilters,
         )

         return result
      }
   }
}

private fun resolveSystemPropertyOrEnvironmentVariable(name: String) = sysprop(name) ?: env(name) ?: ""

internal val TestEngineConfigFiltersFromSystemPropertiesAndEnvironmentInterceptor = run {
   val filterTests = resolveSystemPropertyOrEnvironmentVariable(KotestEngineProperties.filterTests)
   val filterSpecs = resolveSystemPropertyOrEnvironmentVariable(KotestEngineProperties.filterSpecs)

   createTestEngineConfigFiltersProcessor(
      testFiltersSetting = filterTests,
      specFiltersSetting = filterSpecs,
   )
}
