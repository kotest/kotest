package io.kotest.engine

import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.core.spec.afterProject
import io.kotest.core.spec.beforeProject
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.config.dumpProjectConfig
import io.kotest.engine.config.loadAndApplySystemProps
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.launchers.specLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.script.ScriptExecutor
import io.kotest.engine.spec.SpecExecutor
import io.kotest.engine.spec.sort
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlin.reflect.KClass
import kotlin.script.templates.standard.ScriptTemplateWithArgs

data class KotestEngineConfig(
   val filters: List<TestFilter>,
   val listener: TestEngineListener,
   val tags: Tags?,
   val dumpConfig: Boolean,
)

data class TestPlan(val classes: List<KClass<out Spec>>, val scripts: List<KClass<out ScriptTemplateWithArgs>>)

class KotestEngine(private val config: KotestEngineConfig) {

   init {

      ConfigManager.init()

      // if the engine was invoked with explicit tags, we register those via a tag extension
      config.tags?.let { configuration.registerExtension(SpecifiedTagsTagExtension(it)) }

      // if the engine was invoked with explicit filters, those are registered here
      configuration.registerFilters(config.filters)

      // load and apply system properties from [KotestPropertiesFilename]
      loadAndApplySystemProps()
   }

   /**
    * Starts execution of the given test plan.
    */
   suspend fun execute(plan: TestPlan) {

      if (config.dumpConfig) {
         dumpConfig()
      }

      notifyListenerEngineStarted(plan)
         .flatMap { configuration.listeners().beforeProject() }
         .fold(
            { error ->
               // any exception here is swallowed, as we already have an exception to report
               configuration.listeners().afterProject().fold(
                  { end(listOf(error, it)) },
                  {
                     end(it + error)
                  }
               )
               return
            },
            { errors ->
               if (errors.isNotEmpty()) {
                  configuration.listeners().afterProject().fold(
                     { end(errors + listOf(it)) },
                     { end(errors + it) }
                  )
                  return
               }


            }
         )

      submitAll(plan)
         .fold(
            { error ->
               log(error) { "KotestEngine: Error during submit all" }
               configuration.listeners().afterProject().fold(
                  { end(listOf(error, it)) },
                  { end(it + error) }
               )
            },
            {
               // any exception here is used to notify the listener
               configuration.listeners().afterProject().fold(
                  { end(listOf(it)) },
                  { end(it) }
               )

            }
         )
   }

   fun cleanup() {
      configuration.deregisterFilters(config.filters)
   }

   fun dumpConfig() {
      // outputs the engine settings to the console
      configuration.dumpProjectConfig()
   }

   private fun notifyListenerEngineStarted(plan: TestPlan) = Try { config.listener.engineStarted(plan.classes) }

   private suspend fun submitAll(plan: TestPlan) = Try {
      log { "KotestEngine: Beginning test plan [specs=${plan.classes.size}, scripts=${plan.scripts.size}, parallelism=${configuration.parallelism}}]" }

      // scripts always run sequentially
      log { "KotestEngine: Launching ${plan.scripts.size} scripts" }
      if (plan.scripts.isNotEmpty()) {
         plan.scripts.forEach { scriptKClass ->
            log { scriptKClass.java.methods.toList().toString() }
            ScriptExecutor(config.listener)
               .execute(scriptKClass)
         }
         log { "KotestEngine: Script execution completed" }
      }

      // spec classes are ordered using an instance of SpecExecutionOrder
      log { "KotestEngine: Sorting specs by ${configuration.specExecutionOrder}" }
      val ordered = plan.classes.sort(configuration.specExecutionOrder)

      val executor = SpecExecutor(config.listener)
      log { "KotestEngine: Will use spec executor $executor" }

      val launcher = specLauncher()
      log { "KotestEngine: Will use spec launcher $launcher" }

      launcher.launch(executor, ordered)
   }

   private fun end(errors: List<Throwable>) {
      errors.forEach {
         log(it) { "KotestEngine: Error during test engine run" }
         it.printStackTrace()
      }
      config.listener.engineFinished(errors)
      // explicitly exit because we spin up test threads that the user may have put into deadlock
      // exitProcess(if (errors.isEmpty()) 0 else -1)
   }
}
