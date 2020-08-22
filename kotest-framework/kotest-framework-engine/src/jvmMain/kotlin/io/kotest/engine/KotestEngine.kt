package io.kotest.engine

import io.kotest.core.Tags
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
import io.kotest.engine.spec.isDoNotParallelize
import io.kotest.engine.spec.sort
import io.kotest.fp.Try
import io.kotest.mpp.NamedThreadFactory
import io.kotest.mpp.log
import kotlinx.coroutines.runBlocking
import java.util.Collections.emptyList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

data class KotestEngineConfig(
   val filters: List<TestFilter>,
   val listener: TestEngineListener,
   val tags: Tags?,
   val dumpConfig: Boolean,
)

data class TestPlan(val classes: List<KClass<out Spec>>)

class KotestEngine(private val config: KotestEngineConfig) {

   private val specExecutor = SpecExecutor(config.listener)

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

   private fun submitAll(plan: TestPlan) = Try {
      log("Submitting ${plan.classes.size} specs")

      // the classes are ordered using an instance of SpecExecutionOrder
      val ordered = plan.classes.sort(configuration.specExecutionOrder)

      // if parallelize is enabled, then we must order the specs into two sets, depending on if they
      // are thread safe or not.
      val (single, parallel) = if (configuration.parallelism == 1)
         ordered to emptyList()
      else
         ordered.partition { it.isDoNotParallelize() }

      if (parallel.isNotEmpty()) submitBatch(parallel, configuration.parallelism)
      if (single.isNotEmpty()) submitBatch(single, 1)
   }

   private fun submitBatch(specs: List<KClass<out Spec>>, parallelism: Int) {
      val executor = Executors.newFixedThreadPool(
         parallelism,
         NamedThreadFactory("kotest-engine-%d")
      )
      specs.forEach { klass ->
         executor.submit {
            runBlocking {
               specExecutor.execute(klass)
            }
         }
      }
      executor.shutdown()
      log("Waiting for specs execution to terminate")

      try {
         executor.awaitTermination(1, TimeUnit.DAYS)
         log("Spec executor has terminated")
      } catch (t: InterruptedException) {
         log("Spec executor interrupted", t)
         throw t
      }
   }

   private fun end(errors: List<Throwable>) {
      errors.forEach {
         log("Error during test engine run", it)
         it.printStackTrace()
      }
      config.listener.engineFinished(errors)
      // explicitly exit because we spin up test threads that the user may have put into deadlock
      // exitProcess(if (t == null) 0 else -1)
   }
}
