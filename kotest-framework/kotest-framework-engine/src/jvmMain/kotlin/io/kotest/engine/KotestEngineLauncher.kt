package io.kotest.engine

import io.kotest.core.Tags
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.IsolationTestEngineListener
import io.kotest.engine.listener.SynchronizedTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass
import kotlin.script.templates.standard.ScriptTemplateWithArgs

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
   private val scripts: List<KClass<out ScriptTemplateWithArgs>>,
) {

   init {
      ConfigManager.init()
   }

   constructor() : this(emptyList(), emptyList(), emptyList(), null, false, emptyList())

   fun launch(): EngineResult {

      if (listeners.isEmpty())
         error("Cannot launch a KotestEngine without at least one TestEngineListener")

      val config = KotestEngineConfig(
         filters,
         SynchronizedTestEngineListener(
            IsolationTestEngineListener(
               CompositeTestEngineListener(listeners)
            )
         ),
         tags,
         dumpConfig
      )
      val runner = KotestEngine(config)
      val plan = TestSuite(specs, scripts)

      return try {
         runBlocking { // blocks the calling thread while the engine runs
            val result = runner.execute(plan)
            runner.cleanup()
            result
         }
      } catch (e: Exception) {
         e.printStackTrace()
         EngineResult(listOf(e))
      }
   }

   fun withFilter(filter: TestFilter) = withFilters(listOf(filter))

   fun withListener(listener: TestEngineListener) = KotestEngineLauncher(
      listeners = this.listeners + listener,
      specs = specs,
      filters = this.filters + filters,
      tags = tags,
      dumpConfig = dumpConfig,
      scripts = scripts,
   )

   fun withDumpConfig(dump: Boolean) = KotestEngineLauncher(
      listeners = listeners,
      specs = specs,
      filters = this.filters + filters,
      tags = tags,
      dumpConfig = dump,
      scripts = scripts,
   )

   fun withFilters(filters: List<TestFilter>): KotestEngineLauncher {
      return KotestEngineLauncher(
         listeners = listeners,
         specs = specs,
         filters = this.filters + filters,
         tags = tags,
         dumpConfig = dumpConfig,
         scripts = scripts,
      )
   }

   fun withScripts(scripts: List<KClass<out ScriptTemplateWithArgs>>): KotestEngineLauncher {
      return KotestEngineLauncher(
         listeners = listeners,
         specs = specs,
         filters = filters,
         tags = tags,
         dumpConfig = dumpConfig,
         scripts = scripts,
      )
   }

   fun withSpec(klass: KClass<out Spec>) = withSpecs(listOf(klass))

   fun withSpecs(vararg specs: KClass<out Spec>) = withSpecs(specs.toList())

   fun withSpecs(specs: List<KClass<out Spec>>): KotestEngineLauncher {
      return KotestEngineLauncher(
         listeners = listeners,
         specs = specs,
         filters = filters,
         tags = tags,
         dumpConfig = dumpConfig,
         scripts = scripts,
      )
   }

   fun withTags(tags: Tags?): KotestEngineLauncher {
      return KotestEngineLauncher(
         listeners = listeners,
         specs = specs,
         filters = filters,
         tags = tags,
         dumpConfig = dumpConfig,
         scripts = scripts,
      )
   }

}
