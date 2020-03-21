package io.kotest.core.engine

import io.kotest.core.Tag
import io.kotest.core.annotation.Ignored
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
import io.kotest.mpp.hasAnnotation
import io.kotest.mpp.log
import kotlinx.coroutines.runBlocking
import java.util.Collections.emptyList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class KotestEngine(
   val classes: List<KClass<out Spec>>,
   filters: List<TestCaseFilter>,
   val parallelism: Int,
   includedTags: Set<Tag>,
   excludedTags: Set<Tag>,
   val listener: TestEngineListener,
   // added to listeners statically added via Project.add
   val listeners: List<Listener> = emptyList()
) {

   private val specExecutor = SpecExecutor(listener)

   init {
      Project.registerFilters(filters)
      if (includedTags.isNotEmpty() || excludedTags.isNotEmpty())
         Project.registerExtension(
            SpecifiedTagsTagExtension(
               includedTags,
               excludedTags
            )
         )
   }

   private fun notifyTestEngineListener() = Try { listener.engineStarted(classes) }

   private fun submitAll() = Try {
      log("Submitting ${classes.size} specs")

      // any spec that is annotated with @Ignored is filtered out at this stage
      val filtered = classes.filterNot { it.hasAnnotation<Ignored>() }

      // the classes are ordered using an instance of SpecExecutionOrder
      val ordered = Project.specExecutionOrder().sort(filtered)

      // if parallelize is enabled, then we must order the specs into two sets, depending on if they
      // are thread safe or not.
      val (single, parallel) = if (parallelism == 1)
         ordered to emptyList()
      else
         ordered.partition { it.isDoNotParallelize() }

      submitBatch(parallel, parallelism)
      submitBatch(single, 1)
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
      log("Waiting for spec execution to terminate")

      val error = try {
         executor.awaitTermination(1, TimeUnit.DAYS)
         null
      } catch (t: InterruptedException) {
         log("Spec executor interupted", t)
         t
      }

      log("Spec executor has terminated $error")

      if (error != null) throw error
   }

   private fun end(t: Throwable?) = Try {
      if (t != null) {
         log("Error during test engine run", t)
         t.printStackTrace()
      }
      listener.engineFinished(t)
      // explicitly exit because we spin up test threads that the user may have put into deadlock
      // exitProcess(if (t == null) 0 else -1)
   }

   fun execute() {
      notifyTestEngineListener()
         .flatMap { (listeners + Project.listeners()).beforeProject() }
         .flatMap { submitAll() }
         .fold(
            {
               // any exception here is swallowed, as we already have an exception to report
               (listeners + Project.listeners()).afterProject()
               end(it)
            },
            {
               // any exception here is used to notify the listener
               (listeners + Project.listeners()).afterProject().fold(
                  { t -> end(t) },
                  { end(null) }
               )
            }
         )
   }
}
