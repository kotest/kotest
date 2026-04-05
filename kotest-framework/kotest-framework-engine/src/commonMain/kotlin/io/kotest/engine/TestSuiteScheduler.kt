package io.kotest.engine

import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.core.LogLine
import io.kotest.core.Logger
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.Parallel
import io.kotest.core.project.TestSuite
import io.kotest.core.spec.SpecRef
import io.kotest.engine.concurrency.ConcurrencyOrder
import io.kotest.engine.concurrency.isIsolate
import io.kotest.engine.concurrency.isParallel
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.spec.execution.SpecRefExecutor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * A [TestSuiteScheduler] schedules specs for execution using a [SpecRefExecutor].
 * The scheduler handles concurrency depending on the [io.kotest.engine.concurrency.SpecExecutionMode].
 *
 * Additionally, on JVM targets, it will recognize the [Isolate] and [Parallel]
 * annotations to ensure those specs are never/always scheduled concurrently.
 */
internal class TestSuiteScheduler(private val context: TestEngineContext) {

   private val logger = Logger(TestSuiteScheduler::class)
   private val projectConfigResolver = ProjectConfigResolver(context.projectConfig, context.registry)

   suspend fun schedule(suite: TestSuite) {
      logger.log { "Launching ${suite.specs.size} specs" }

      val isolated = suite.specs.filter { it.kclass.isIsolate() }
      val parallel = suite.specs.filter { it.kclass.isParallel() }
      // the rest of the specs use the default concurrency mode
      val default = suite.specs.filter { !it.kclass.isIsolate() && !it.kclass.isParallel() }

      logger.log { "Partitioned into ${isolated.size} isolated, ${parallel.size} parallel, ${default.size} default" }
      val order = projectConfigResolver.concurrencyOrder()
      logger.log { "Concurrency order is $order" }

      when (order) {
         ConcurrencyOrder.IsolateFirst -> {
            schedule(isolated, 1)
            logger.log { "Isolated specs have completed" }

            schedule(parallel, Int.MAX_VALUE)
            logger.log { "Parallelized specs have completed" }

            schedule(default, concurrency())
            logger.log { "Remaining specs have completed" }
         }

         ConcurrencyOrder.IsolateLast -> {
            schedule(default, concurrency())
            logger.log { "Remaining specs have completed" }

            schedule(parallel, Int.MAX_VALUE)
            logger.log { "Parallelized specs have completed" }

            schedule(isolated, 1)
            logger.log { "Isolated specs have completed" }
         }
      }
   }

   private suspend fun schedule(
      specs: List<SpecRef>,
      concurrency: Int,
   ) {

      val semaphore = Semaphore(concurrency)
      logger.log { "Scheduling using concurrency: $concurrency" }

      coroutineScope { // we don't want this function to return until all specs are completed
         specs.map { ref ->
            logger.log { LogLine(ref.fqn, "Scheduling coroutine") }
            launch {
               semaphore.withPermit {
                  logger.log { LogLine(ref.fqn, "Acquired permit") }
                  executeIfNotFailedFast(ref, context.collector)
               }
               logger.log { LogLine(ref.fqn, "Released permit") }
            }
         }
      }
   }

   private suspend fun executeIfNotFailedFast(
      ref: SpecRef,
      collector: CollectingTestEngineListener,
   ) {
      if (projectConfigResolver.projectWideFailFast() && collector.errors) {
         logger.log { LogLine(ref.fqn, "Project wide fail fast is active, skipping spec") }
         context.listener.specIgnored(ref.kclass, null)
      } else {
         try {
            val executor = SpecRefExecutor(context)
            logger.log { LogLine(ref.fqn, "Executing ref") }
            executor.execute(ref)
         } catch (t: Throwable) {
            logger.log { LogLine(ref.fqn, "Unhandled error during spec execution $t") }
            throw t
         }
      }
   }

   /**
    * Returns the max concurrent specs to execute.
    *
    * On non-JVM platforms, this will always be 1, otherwise the value
    * of [io.kotest.engine.concurrency.SpecExecutionMode] from project configuration is used.
    */
   private fun concurrency(): Int {
      return when (platform) {
         Platform.JVM -> projectConfigResolver.specExecutionMode().concurrency
         else -> 1
      }
   }
}
