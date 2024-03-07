package io.kotest.engine

import io.kotest.common.ExperimentalKotest
import io.kotest.core.annotation.DoNotParallelize
import io.kotest.core.annotation.Isolate
import io.kotest.core.concurrency.use
import io.kotest.core.project.TestSuite
import io.kotest.core.spec.SpecRef
import io.kotest.engine.concurrency.defaultCoroutineDispatcherFactory
import io.kotest.engine.concurrency.isIsolate
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * A [TestSuiteScheduler] that schedules specs concurrently, up to a provided [maxConcurrent] value.
 * If the value is 1 then this scheduler will execute specs strictly sequentially.
 *
 * Additionally, on JVM targets, it will recognize the [Isolate] and [DoNotParallelize]
 * annotations to ensure those specs are never scheduled concurrently.
 *
 * @param maxConcurrent The maximum number of concurrent coroutines.
 */
@ExperimentalKotest
internal class ConcurrentTestSuiteScheduler(
   private val maxConcurrent: Int,
   private val context: EngineContext,
) : TestSuiteScheduler {

   private val logger = Logger(ConcurrentTestSuiteScheduler::class)

   override suspend fun schedule(suite: TestSuite): EngineResult {
      logger.log { Pair(null, "Launching ${suite.specs.size} specs") }

      val (sequential, concurrent) = suite.specs.partition { it.kclass.isIsolate() }
      logger.log { Pair(null, "Split on isIsolate: ${sequential.size} sequential ${concurrent.size} concurrent") }

      schedule(concurrent, maxConcurrent)
      logger.log { Pair(null, "Concurrent specs have completed") }

      schedule(sequential, 1)
      logger.log { Pair(null, "Sequential specs have completed") }

      return EngineResult(emptyList())
   }

   private suspend fun schedule(
      specs: List<SpecRef>,
      concurrency: Int,
   ) = defaultCoroutineDispatcherFactory(context.configuration).use { coroutineDispatcherFactory ->
      coroutineScope { // we don't want this function to return until all specs are completed
         val semaphore = Semaphore(concurrency)
         val collector = CollectingTestEngineListener()
         specs.map { ref ->
            logger.log { Pair(ref.kclass.bestName(), "Scheduling coroutine") }
            launch {
               semaphore.withPermit {
                  logger.log { Pair(ref.kclass.bestName(), "Acquired permit") }

                  if (context.configuration.projectWideFailFast && collector.errors) {
                     context.listener.specIgnored(ref.kclass, null)
                  } else {
                     try {
                        val executor = SpecExecutor(coroutineDispatcherFactory, context.mergeListener(collector))
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
}
