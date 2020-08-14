package io.kotest.engine.launcher

import io.kotest.core.Tags
import io.kotest.core.engine.KotestEngine
import io.kotest.core.engine.KotestEngineConfig
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.engine.discovery.Discovery
import io.kotest.core.engine.discovery.DiscoveryRequest
import io.kotest.core.engine.discovery.DiscoverySelector
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * The contract on this class cannot change without breaking the kotest plugin.
 * It must remain a backwards compatible layer between the launchers and the engine.
 */
class KotestEngineLauncher(
   private val listener: TestEngineListener,
   private val specs: List<KClass<out Spec>>,
   private val filters: List<TestFilter>,
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

   fun addFilter(filter: TestFilter) = addFilters(listOf(filter))

   fun addFilters(filters: List<TestFilter>): KotestEngineLauncher {
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

   fun forPackage(packageName: String) = addSelector(DiscoverySelector.PackageDiscoverySelector(packageName))
}
