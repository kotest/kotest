package io.kotest.core.engine

import io.kotest.core.Tags
import io.kotest.core.engine.discovery.Discovery
import io.kotest.core.engine.discovery.DiscoveryRequest
import io.kotest.core.engine.discovery.DiscoverySelector
import io.kotest.core.filters.TestCaseFilter
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

class KotestEngineLauncher(
   private val listener: TestEngineListener,
   private val specs: List<KClass<out Spec>>,
   private val filters: List<TestCaseFilter>,
   private val tags: Tags?,
   private val selectors: List<DiscoverySelector>
) {

   constructor(listener: TestEngineListener) : this(listener, emptyList(), emptyList(), null, emptyList())

   suspend fun launch() {

      val specs = when {
         specs.isNotEmpty() -> specs
         selectors.isNotEmpty() -> Discovery.discover(DiscoveryRequest(selectors = selectors)).specs
         else -> Discovery.discover(DiscoveryRequest()).specs
      }

      val config = KotestEngineConfig(specs, filters, listener, tags)
      val runner = KotestEngine(config)

      runner.execute()
      try {
         runner.cleanup()
      } catch (e: Exception) {
      }
   }

   fun addFilter(filter: TestCaseFilter) = addFilters(listOf(filter))

   fun addFilters(filters: List<TestCaseFilter>): KotestEngineLauncher {
      return KotestEngineLauncher(
         listener = listener,
         specs = specs,
         filters = this.filters + filters,
         tags = tags,
         selectors = selectors
      )
   }

   fun withSpecs(specs: List<KClass<out Spec>>): KotestEngineLauncher {
      return KotestEngineLauncher(
         listener = listener,
         specs = specs,
         filters = filters,
         tags = tags,
         selectors = selectors
      )
   }

   fun withTags(tags: Tags?): KotestEngineLauncher {
      return KotestEngineLauncher(
         listener = listener,
         specs = specs,
         filters = filters,
         tags = tags,
         selectors = selectors
      )
   }

   fun forSpec(klass: KClass<out Spec>) = withSpecs(listOf(klass))

   fun addSelector(selector: DiscoverySelector): KotestEngineLauncher {
      return KotestEngineLauncher(
         listener = listener,
         specs = specs,
         filters = filters,
         tags = tags,
         selectors = selectors + selector
      )
   }
}
