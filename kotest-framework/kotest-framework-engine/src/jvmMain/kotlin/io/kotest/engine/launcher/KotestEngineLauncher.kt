package io.kotest.engine.launcher

import io.kotest.core.Tags
import io.kotest.engine.KotestEngine
import io.kotest.engine.KotestEngineConfig
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.engine.TestPlan
import kotlin.reflect.KClass

/**
 * A builder class for creating and executing tests via the [KotestEngine].
 *
 * The contract on this class cannot change without breaking the kotest plugin.
 * It must remain a backwards compatible layer between the launchers and the engine.
 */
class KotestEngineLauncher(
   private val listener: TestEngineListener,
   private val specs: List<KClass<out Spec>>,
   private val filters: List<TestFilter>,
   private val tags: Tags?
) {

   constructor(listener: TestEngineListener) : this(listener, emptyList(), emptyList(), null)

   suspend fun launch() {

      val config = KotestEngineConfig(filters, listener, tags)
      val runner = KotestEngine(config)
      val plan = TestPlan(specs)

      try {
         runner.execute(plan)
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
         tags = tags
      )
   }

   fun withSpec(klass: KClass<out Spec>) = withSpecs(listOf(klass))
   fun withSpecs(specs: List<KClass<out Spec>>): KotestEngineLauncher {
      return KotestEngineLauncher(
         listener = listener,
         specs = specs,
         filters = filters,
         tags = tags
      )
   }

   fun withTags(tags: Tags?): KotestEngineLauncher {
      return KotestEngineLauncher(
         listener = listener,
         specs = specs,
         filters = filters,
         tags = tags
      )
   }

}
