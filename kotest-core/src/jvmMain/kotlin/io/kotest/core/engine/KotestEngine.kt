package io.kotest.core.engine

import io.kotest.core.Tags
import io.kotest.core.config.Project
import io.kotest.core.extensions.SpecifiedTagsTagExtension
import io.kotest.core.filters.TestCaseFilter
import io.kotest.core.internal.NamedThreadFactory
import io.kotest.core.listeners.Listener
import io.kotest.core.runtime.afterProject
import io.kotest.core.runtime.beforeProject
import io.kotest.core.spec.Spec
import io.kotest.core.spec.isDoNotParallelize
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlinx.coroutines.runBlocking
import java.util.Collections.emptyList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class KotestEngine(
   val classes: List<KClass<out Spec>>,
   val filters: List<TestCaseFilter>,
   val parallelism: Int,
   tags: Tags?,
   val listener: TestEngineListener,
   // added to listeners statically added via Project.add
   val listeners: List<Listener> = emptyList()
) {

   private val specExecutor = SpecExecutor(listener)

   init {
      Project.registerFilters(filters)
      if (tags != null)
         Project.registerExtension(SpecifiedTagsTagExtension(tags))
   }

   fun cleanup() {
      Project.deregisterFilters(filters)
   }

   private fun notifyTestEngineListener() = Try { listener.engineStarted(classes) }

   private fun submitAll() = Try {
       log("Submitting ${classes.size} specs")

       // the classes are ordered using an instance of SpecExecutionOrder
       val ordered = Project.specExecutionOrder().sort(classes)

       // if parallelize is enabled, then we must order the specs into two sets, depending on if they
       // are thread safe or not.
       val (single, parallel) = if (parallelism == 1)
           ordered to emptyList()
       else
           ordered.partition { it.isDoNotParallelize() }

       if (parallel.isNotEmpty()) submitBatch(parallel, parallelism)
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
      listener.engineFinished(errors)
      // explicitly exit because we spin up test threads that the user may have put into deadlock
      // exitProcess(if (t == null) 0 else -1)
   }

   suspend fun execute() {
      notifyTestEngineListener()
         .flatMap { (listeners + Project.listeners()).beforeProject() }
         .fold(
            { error ->
               // any exception here is swallowed, as we already have an exception to report
               (listeners + Project.listeners()).afterProject().fold(
                  { end(listOf(error, it)) },
                  {
                     end(it + error)
                  }
               )
               return
            },
            { errors ->
               if (errors.isNotEmpty()) {
                  (listeners + Project.listeners()).afterProject().fold(
                     { end(errors + listOf(it)) },
                     { end(errors + it) }
                  )
                  return
               }


            }
         )

      Try { submitAll() }
         .fold(
            { error ->
               (listeners + Project.listeners()).afterProject().fold(
                  { end(listOf(error, it)) },
                  { end(it + error) }
               )
            },
            {
               // any exception here is used to notify the listener
               (listeners + Project.listeners()).afterProject().fold(
                  { end(listOf(it)) },
                  { end(it) }
               )

            }
         )
   }
}
