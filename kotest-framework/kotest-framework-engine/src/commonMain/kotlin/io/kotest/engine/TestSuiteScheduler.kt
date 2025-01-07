@file:Suppress("DEPRECATION")

package io.kotest.engine

import io.kotest.core.Logger
import io.kotest.core.Platform
import io.kotest.core.annotation.DoNotParallelize
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.Parallel
import io.kotest.core.platform
import io.kotest.core.project.TestSuite
import io.kotest.core.spec.SpecRef
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.mpp.IncludingAnnotations
import io.kotest.mpp.IncludingSuperclasses
import io.kotest.mpp.bestName
import io.kotest.mpp.hasAnnotation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.reflect.KClass

/**
 * A [TestSuiteScheduler] schedules specs for execution using a [SpecExecutor].
 * The scheduler handles concurrency depending on the [io.kotest.engine.concurrency.SpecExecutionMode].
 *
 * Additionally, on JVM targets, it will recognize the [Isolate] and [Parallel]
 * annotations to ensure those specs are never/always scheduled concurrently.
 */
internal class TestSuiteScheduler(
   private val context: EngineContext,
) {

   private val logger = Logger(TestSuiteScheduler::class)

   suspend fun schedule(suite: TestSuite): EngineResult {
      logger.log { Pair(null, "Launching ${suite.specs.size} specs") }

      val isolated = suite.specs.filter { it.kclass.isIsolate() }
      logger.log { Pair(null, "Isolated spec count: ${isolated.size}") }
      schedule(isolated, 1)
      logger.log { Pair(null, "Isolated specs have completed") }

      val parallel = suite.specs.filter { it.kclass.isParallel() }
      logger.log { Pair(null, "Parallelized spec count: ${parallel.size}") }
      schedule(parallel, Int.MAX_VALUE)
      logger.log { Pair(null, "Parallelized specs have completed") }

      val default = suite.specs.filter { !it.kclass.isIsolate() && !it.kclass.isParallel() }
      logger.log { Pair(null, "Remaining spec count: ${default.size}") }
      schedule(default, concurrency())
      logger.log { Pair(null, "Remaining specs have completed") }

      return EngineResult(emptyList())
   }

   private suspend fun schedule(
      specs: List<SpecRef>,
      concurrency: Int,
   ) {

      val collector = CollectingTestEngineListener()

      val semaphore = Semaphore(concurrency)
      logger.log { Pair(null, "Scheduling using concurrency: $concurrency") }

      coroutineScope { // we don't want this function to return until all specs are completed
         specs.map { ref ->
            logger.log { Pair(ref.kclass.bestName(), "Scheduling coroutine") }
            launch {
               semaphore.withPermit {
                  logger.log { Pair(ref.kclass.bestName(), "Acquired permit") }
                  if (context.projectConfigResolver.projectWideFailFast() && collector.errors) {
                     logger.log { Pair(ref.kclass.bestName(), "Project wide fail fast is active, skipping spec") }
                     context.listener.specIgnored(ref.kclass, null)
                  } else {
                     try {
                        val executor = SpecExecutor(context.mergeListener(collector))
                        logger.log { Pair(ref.kclass.bestName(), "Executing ref") }
                        executor.execute(ref)
                     } catch (t: Throwable) {
                        logger.log { Pair(ref.kclass.bestName(), "Unhandled error during spec execution $t") }
                        throw t
                     }
                  }
               }
               logger.log { Pair(ref.kclass.bestName(), "Released permit") }
            }
         }
      }
   }

   /**
    * Returns the max concurrent specs to execute.
    * On non-JVM platforms, this will always be 1, otherwise the value
    * of [io.kotest.engine.concurrency.SpecExecutionMode] from project configuration is used.
    */
   private fun concurrency(): Int {
      return when (platform) {
         Platform.JVM -> context.projectConfigResolver.specExecutionMode().concurrency
         Platform.JS,
         Platform.Native,
         Platform.WasmJs -> 1
      }
   }
}

/**
 * Returns true if this class is annotated with either of the annotations used to indicate
 * this spec should not run concurrently regardless of config.
 *
 * Those annotations are [DoNotParallelize] and [Isolate].
 */
@Suppress("DEPRECATION")
internal fun KClass<*>.isIsolate(): Boolean =
   hasAnnotation<DoNotParallelize>(IncludingAnnotations, IncludingSuperclasses)
      || hasAnnotation<Isolate>(IncludingAnnotations, IncludingSuperclasses)

/**
 * Returns true if this class is annotated with the annotation used to indicate
 * this spec should always run concurrently regardless of config.
 */
internal fun KClass<*>.isParallel() = hasAnnotation<Parallel>(IncludingAnnotations, IncludingSuperclasses)
