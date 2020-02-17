package io.kotest.core.engine

import io.kotest.core.Tag
import io.kotest.core.config.Project
import io.kotest.core.extensions.SpecifiedTagsTagExtension
import io.kotest.core.filters.TestCaseFilter
import io.kotest.core.runtime.afterAll
import io.kotest.core.runtime.beforeAll
import io.kotest.core.spec.Spec
import io.kotest.core.spec.isDoNotParallelize
import io.kotest.fp.Try
import io.kotest.core.internal.NamedThreadFactory
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
   val listener: TestEngineListener
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

      // the classes are ordered using an instance of SpecExecutionOrder
      val specs = Project.specExecutionOrder().sort(classes)

      // if parallelize is enabled, then we must order the specs into two sets, depending on if they
      // are thread safe or not.
      val (single, parallel) = if (parallelism == 1)
         specs to emptyList()
      else
         specs.partition { it.isDoNotParallelize() }

      submitBatch(parallel, parallelism)
      submitBatch(single, 1)
   }

   private fun submitBatch(specs: List<KClass<out Spec>>, parallelism: Int) {
      val executor = Executors.newFixedThreadPool(parallelism,
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
   }

   fun execute() {
      notifyTestEngineListener()
         .flatMap { beforeAll() }
         .flatMap { submitAll() }.fold(
            {
               afterAll()
               end(it)
            },
            {
               afterAll().fold(
                  { t -> end(t) },
                  { end(null) }
               )
            }
         )
   }
}
