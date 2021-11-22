package io.kotest.engine

import io.kotest.common.ExperimentalKotest
import io.kotest.core.ProjectContext
import io.kotest.core.TestSuite
import io.kotest.core.annotation.DoNotParallelize
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.SpecRef
import io.kotest.engine.concurrency.defaultCoroutineDispatcherFactory
import io.kotest.engine.concurrency.isIsolate
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.TestEngineListener
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

   private val logger = Logger(this::class)

   override suspend fun schedule(suite: TestSuite, listener: TestEngineListener): EngineResult {
      logger.log { Pair(null, "Launching ${suite.specs.size} specs") }

      val (sequential, concurrent) = suite.specs.partition { it.kclass.isIsolate() }
      logger.log { Pair(null, "Split on isIsolate: ${sequential.size} sequential ${concurrent.size} concurrent") }

      schedule(concurrent, listener, maxConcurrent)
      logger.log { Pair(null, "Concurrent specs have completed") }

      schedule(sequential, listener, 1)
      logger.log { Pair(null, "Sequential specs have completed") }

      return EngineResult(emptyList())
   }

   private suspend fun schedule(
      specs: List<SpecRef>,
      listener: TestEngineListener,
      concurrency: Int,
   ) = coroutineScope { // we don't want this function to return until all specs are completed
      val coroutineDispatcherFactory = defaultCoroutineDispatcherFactory(context.configuration)
      val semaphore = Semaphore(concurrency)
      specs.forEach { ref ->
         logger.log { Pair(ref.kclass.bestName(), "Scheduling coroutine") }
         launch {
            semaphore.withPermit {
               logger.log { Pair(ref.kclass.bestName(), "Acquired permit") }
               try {
                  val executor = SpecExecutor(
                     listener,
                     coroutineDispatcherFactory,
                     context
                  )
                  logger.log { Pair(ref.kclass.bestName(), "Executing ref") }
                  executor.execute(ref)
               } catch (t: Throwable) {
                  logger.log { Pair(ref.kclass.bestName(), "Unhandled error during spec execution $t") }
                  throw t
               }
            }
            logger.log { Pair(ref.kclass.bestName(), "Released permit") }
         }
      }
   }
}
