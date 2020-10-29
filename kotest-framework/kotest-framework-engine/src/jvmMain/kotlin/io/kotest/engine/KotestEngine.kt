package io.kotest.engine

import io.kotest.core.Tags
import io.kotest.core.config.ConcurrencyMode
import io.kotest.core.config.configuration
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.core.spec.afterProject
import io.kotest.core.spec.beforeProject
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.config.dumpProjectConfig
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.core.internal.isIsolate
import io.kotest.engine.spec.sort
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlin.reflect.KClass

data class KotestEngineConfig(
   val filters: List<TestFilter>,
   val listener: TestEngineListener,
   val tags: Tags?,
   val dumpConfig: Boolean,
)

data class TestPlan(val classes: List<KClass<out Spec>>)

class KotestEngine(private val config: KotestEngineConfig) {

   private val executor = SpecExecutor(config.listener)

   init {

      ConfigManager.init()

      // if the engine was invoked with explicit tags, we register those via a tag extension
      config.tags?.let { configuration.registerExtension(SpecifiedTagsTagExtension(it)) }

      // if the engine was invoked with explicit filters, those are registered here
      configuration.registerFilters(config.filters)
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

      Try { submitAll(plan) }
         .fold(
            { error ->
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
      log("KotestEngine: Beginning test plan [specs=${plan.classes.size}, parallelism=${configuration.parallelism}, concurrencyMode=${configuration.concurrencyMode}]")

      // spec classes are ordered using an instance of SpecExecutionOrder
      val ordered = plan.classes.sort(configuration.specExecutionOrder)

      val isParallel = when (configuration.concurrencyMode) {
         ConcurrencyMode.None -> false // explicitly deactivates all concurrency
         ConcurrencyMode.Test -> false // explicitly deactivates spec concurrency
         ConcurrencyMode.Spec -> true // explicitly activated spec concurrency
         ConcurrencyMode.All -> true // explicitly activated all concurrency
         else -> configuration.parallelism > 1 // implicitly activated concurrency
      }

      // if we are operating in a concurrent mode, then we partition the specs into those which
      // can run concurrently (default) and those which cannot (see @Isolated)
      if (isParallel) {
         val (sequential, parallel) = ordered.partition { it.isIsolate() }
         log("KotestEngine: Partitioned specs into ${parallel.size} parallel and ${sequential.size} sequential")

         if (parallel.isNotEmpty()) ConcurrentSpecLauncher(configuration.parallelism).submit(executor, parallel)
         if (sequential.isNotEmpty()) SequentialSpecLauncher.submit(executor, sequential)

      } else {
         if (ordered.isNotEmpty()) SequentialSpecLauncher.submit(executor, ordered)
      }
   }

   private fun end(errors: List<Throwable>) {
      errors.forEach {
         log("KotestEngine: Error during test engine run", it)
         it.printStackTrace()
      }
      config.listener.engineFinished(errors)
      // explicitly exit because we spin up test threads that the user may have put into deadlock
      // exitProcess(if (errors.isEmpty()) 0 else -1)
   }
}
