package io.kotest.runner.jvm

import io.kotest.Project
import io.kotest.core.Tag
import io.kotest.core.test.TestCaseFilter
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.isDoNotParallelize
import io.kotest.extensions.SpecifiedTagsTagExtension
import io.kotest.fp.Try
import io.kotest.runner.jvm.internal.NamedThreadFactory
import io.kotest.runner.jvm.spec.SpecExecutor
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.Collections.emptyList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class KotestEngine(
   val classes: List<KClass<out SpecConfiguration>>,
   filters: List<TestCaseFilter>,
   val parallelism: Int,
   includedTags: Set<Tag>,
   excludedTags: Set<Tag>,
   val listener: TestEngineListener
) {

   private val logger = LoggerFactory.getLogger(this.javaClass)
   private val specExecutor = SpecExecutor(listener)

   init {
      Project.registerTestCaseFilter(filters)
      if (includedTags.isNotEmpty() || excludedTags.isNotEmpty())
         Project.registerExtension(SpecifiedTagsTagExtension(includedTags, excludedTags))
   }

   private fun afterAll() = Try { Project.afterAll() }

   private fun start() = Try {
      listener.engineStarted(classes)
      Project.beforeAll()
   }

   private fun submitAll() = Try {
      logger.trace("Submitting ${classes.size} specs")

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

   private fun submitBatch(specs: List<KClass<out SpecConfiguration>>, parallelism: Int) {
      val executor = Executors.newFixedThreadPool(parallelism, NamedThreadFactory("kotest-engine-%d"))
      specs.forEach { klass ->
         executor.submit {
            runBlocking {
               specExecutor.execute(klass)
            }
         }
      }
      executor.shutdown()
      logger.trace("Waiting for spec execution to terminate")

      val error = try {
         executor.awaitTermination(1, TimeUnit.DAYS)
         null
      } catch (t: InterruptedException) {
         logger.error("Spec executor interupted", t)
         t
      }

      logger.trace("Spec executor has terminated $error")

      if (error != null) throw error
   }

   private fun end(t: Throwable?) = Try {
      if (t != null) {
         logger.error("Error during test engine run", t)
         t.printStackTrace()
      }
      listener.engineFinished(t)
   }

   fun execute() {
      start().flatMap { submitAll() }.fold(
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
