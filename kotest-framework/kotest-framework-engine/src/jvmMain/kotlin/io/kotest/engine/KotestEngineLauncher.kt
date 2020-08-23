package io.kotest.engine

import io.kotest.core.Tags
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.listener.CompositeTestEngineListener
import kotlin.reflect.KClass

/**
 * A builder class for creating and executing tests via the [KotestEngine].
 *
 * The contract on this class cannot change without breaking the kotest plugin.
 * It must remain a backwards compatible layer between the launchers and the engine.
 */
class KotestEngineLauncher(
   private val listeners: List<TestEngineListener>,
   private val specs: List<KClass<out Spec>>,
   private val filters: List<TestFilter>,
   private val tags: Tags?,
   private val dumpConfig: Boolean,
) {

   init {
      ConfigManager.init()
   }

   constructor() : this(emptyList(), emptyList(), emptyList(), null, true)

   suspend fun launch() {

      if (listeners.isEmpty())
         error("Cannot launch a KotestEngine without at least one TestEngineListener")

      val config = KotestEngineConfig(filters, CompositeTestEngineListener(listeners), tags, dumpConfig)
      val runner = KotestEngine(config)
      val plan = TestPlan(specs)

      try {
         runner.execute(plan)
         runner.cleanup()
      } catch (e: Exception) {
      }
   }

   fun withFilter(filter: TestFilter) = withFilters(listOf(filter))

   fun withListener(listener: TestEngineListener) = KotestEngineLauncher(
      listeners = this.listeners + listener,
      specs = specs,
      filters = this.filters + filters,
      tags = tags,
      dumpConfig = dumpConfig,
   )

   fun withDumpConfig(dump: Boolean) = KotestEngineLauncher(
      listeners = listeners,
      specs = specs,
      filters = this.filters + filters,
      tags = tags,
      dumpConfig = dump,
   )

   fun withFilters(filters: List<TestFilter>): KotestEngineLauncher {
      return KotestEngineLauncher(
         listeners = listeners,
         specs = specs,
         filters = this.filters + filters,
         tags = tags,
         dumpConfig = dumpConfig,
      )
   }

   fun withSpec(klass: KClass<out Spec>) = withSpecs(listOf(klass))
   fun withSpecs(specs: List<KClass<out Spec>>): KotestEngineLauncher {
      return KotestEngineLauncher(
         listeners = listeners,
         specs = specs,
         filters = filters,
         tags = tags,
         dumpConfig = dumpConfig,
      )
   }

   fun withTags(tags: Tags?): KotestEngineLauncher {
      return KotestEngineLauncher(
         listeners = listeners,
         specs = specs,
         filters = filters,
         tags = tags,
         dumpConfig = dumpConfig,
      )
   }

}
