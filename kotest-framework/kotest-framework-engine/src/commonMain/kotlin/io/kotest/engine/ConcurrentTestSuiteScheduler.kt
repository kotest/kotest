package io.kotest.engine

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.Configuration
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.SpecRef
import io.kotest.engine.concurrency.defaultCoroutineDispatcherFactory
import io.kotest.engine.concurrency.isIsolate
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.mpp.log
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
   private val configuration: Configuration,
) : TestSuiteScheduler {

   override suspend fun schedule(suite: TestSuite, listener: TestEngineListener): EngineResult {
      log { "DefaultTestSuiteScheduler: Launching ${suite.specs.size} specs" }

      val (sequential, concurrent) = suite.specs.partition { it.kclass.isIsolate() }
      log { "DefaultTestSuiteScheduler: Split specs based on isolation annotations [${sequential.size} sequential ${concurrent.size} concurrent]" }

      schedule(concurrent, listener, maxConcurrent)
      log { "DefaultSpecLauncher: Concurrent specs have completed" }

      schedule(sequential, listener, 1)
      log { "DefaultSpecLauncher: Sequential specs have completed" }

      return EngineResult(emptyList())
   }

   private suspend fun schedule(
      specs: List<SpecRef>,
      listener: TestEngineListener,
      concurrency: Int,
   ) = coroutineScope { // we don't want this function to return until all specs are completed
      val coroutineDispatcherFactory = defaultCoroutineDispatcherFactory(configuration)
      val semaphore = Semaphore(concurrency)
      specs.forEach { ref ->
         log { "DefaultTestSuiteScheduler: Scheduling coroutine for spec [$ref]" }
         launch {
            semaphore.withPermit {
               log { "DefaultTestSuiteScheduler: Acquired permit for $ref" }
               try {
                  val executor = SpecExecutor(listener, coroutineDispatcherFactory, configuration)
                  executor.execute(ref)
               } catch (t: Throwable) {
                  log { "DefaultTestSuiteScheduler: Unhandled error during spec execution [$ref] [$t]" }
                  throw t
               }
            }
         }
      }
   }
}
