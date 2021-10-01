package io.kotest.engine.extensions

import io.kotest.common.KotestInternal
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.SpecFilterResult
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.engine.TestEngineConfig
import io.kotest.fp.Try
import io.kotest.mpp.env
import io.kotest.mpp.sysprop
import kotlin.reflect.KClass

const val testFiltersProperty = "kotest.filters.tests"
const val specFiltersProperty = "kotest.filters.specs"

private fun Regex.toTestFilter(): TestFilter = object : TestFilter {
   override fun filter(descriptor: Descriptor): TestFilterResult {
      val regex = this@toTestFilter
      val name = descriptor.path().value

      return if (regex.matches(name)) TestFilterResult.Include else TestFilterResult.Exclude
   }
}

private fun Regex.toSpecFilter(): SpecFilter = object : SpecFilter {
   override fun filter(kclass: KClass<*>): SpecFilterResult {
      val regex = this@toSpecFilter
      val name = kclass.simpleName ?: ""

      return if (regex.matches(name)) SpecFilterResult.Include else SpecFilterResult.Exclude
   }
}

fun readFilterPropertyToRegexes(name: String): Sequence<Regex> = (sysprop(name) ?: env(name) ?: "")
   .split(",")
   .asSequence()
   .filter { it.isNotBlank() }
   .map { Try { Regex(it) } }
   .mapNotNull { it.valueOrNull() }

@OptIn(KotestInternal::class)
object TestEngineConfigFiltersExtension : TestEngineConfigExtension {
   override fun transform(config: TestEngineConfig): TestEngineConfig {
      val testFilters = readFilterPropertyToRegexes(testFiltersProperty).map { it.toTestFilter() } + config.testFilters
      val specFilters = readFilterPropertyToRegexes(specFiltersProperty).map { it.toSpecFilter() } + config.specFilters

      return config.copy(
         testFilters = testFilters.toList(),
         specFilters = specFilters.toList(),
      )
   }
}
