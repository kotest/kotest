package io.kotest.runner.jvm

import io.kotest.core.DoNotParallelize
import io.kotest.core.Project
import io.kotest.core.tags.Tag
import io.kotest.core.TestCaseFilter
import io.kotest.core.fp.Try
import io.kotest.core.specs.SpecContainer
import io.kotest.extensions.SpecifiedTagsTagExtension
import io.kotest.runner.jvm.internal.NamedThreadFactory
import io.kotest.runner.jvm.spec.SpecExecutor
import org.slf4j.LoggerFactory
import java.util.Collections.emptyList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.findAnnotation

class TestEngine(
   val specs: List<SpecContainer>,
   filters: List<TestCaseFilter>,
   val parallelism: Int,
   includedTags: Set<Tag>,
   excludedTags: Set<Tag>,
   val listener: TestEngineListener
) {

   private val logger = LoggerFactory.getLogger(this.javaClass)

   // the scheduler executor is used for notifications when a test case timeout has been reached
   private val scheduler = Executors.newSingleThreadScheduledExecutor()

   private val specExecutor = SpecExecutor(listener, scheduler)

   // as the test engine is created we check the project config for any dynamic test filters and tags
   init {
      Project.registerTestCaseFilter(filters)
      if (includedTags.isNotEmpty() || excludedTags.isNotEmpty())
         Project.registerExtension(SpecifiedTagsTagExtension(includedTags, excludedTags))
   }

   fun execute() {
      start()
         .flatMap { submitAll() }
         .fold(
            // if we already had an error, we just wrap up the engine callbacks, and use that error to wrap up
            // (so we can ditch any after all error)
            {
               afterAll()
               end(it)
            },
            // if we didn't have any errors, then we will only fail with an error if the afterAll itself fails
            {
               afterAll().fold(
                  { end(it) },
                  { end(null) }
               )
            }
         )
   }

   // attempts to submit all specs to the test engine
   private fun submitAll() = Try {
      logger.trace("Submitting ${specs.size} specs")

      val ordered = Project.specExecutionOrder().sort(specs)

      // if parallelization is enabled, then we must order the specs into two sets, depending on whether those
      // specs are specified to be thread safe or not (default is thread safe).
      // the thread safe tests can run in parallel first and the non-thread safe ones can run sequentially after
      val (single, parallel) = if (parallelism == 1)
         ordered to emptyList()
      else
         ordered.partition { it.isDoNotParallelize() }

      submitBatch(parallel, parallelism)
      submitBatch(single, 1)
   }

   private fun submitBatch(specs: List<SpecContainer>, parallelism: Int) {
      val executor = Executors.newFixedThreadPool(
         parallelism,
         NamedThreadFactory("kotest-engine-%d")
      )
      specs.forEach { submitSpec(it, executor) }
      executor.shutdown()

      logger.trace("Waiting for spec executor to terminate")
      val error = try {
         executor.awaitTermination(1, TimeUnit.DAYS)
         null
      } catch (t: InterruptedException) {
         t
      }

      if (error != null) throw error
   }

   // attempt to invoke the engine finished callback with an optional failure
   private fun end(t: Throwable?) = Try {
      if (t != null) {
         logger.error("Error during test engine run", t)
         t.printStackTrace()
      }
      listener.engineFinished(t)
   }

   // attempt to execute the project after all callbacks
   private fun afterAll() = Try { Project.afterAll() }

   // attempt to invoke the engine started callback
   private fun start() = Try {
      listener.engineStarted2(specs)
      Project.beforeAll()
   }

   private fun submitSpec(container: SpecContainer, executor: ExecutorService) {
      executor.submit {
         createSpec(container)
            .flatMap { specExecutor.execute(it) }
            .onFailure {
               listener.specExecutionError(container, it)
               // if creating or executing the spec failed we will bomb out early as this means
               // something happened that the framwork wasn't able to handle
               executor.shutdownNow()
            }
      }
   }

   private fun createSpec(container: SpecContainer) = container.instantiate().flatMap {
      Try {
         // todo restore this for full junit support
         // listener.specCreated(it)
         it
      }
   }
}

fun SpecContainer.isDoNotParallelize(): Boolean = when (this) {
   is SpecContainer.ValueSpec -> true
   is SpecContainer.ClassSpec -> this.kclass.findAnnotation<DoNotParallelize>() != null
}
