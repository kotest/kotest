package io.kotest.engine

import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.SpecRef
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
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
class ConcurrentTestSuiteScheduler(private val maxConcurrent: Int) : TestSuiteScheduler {

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
      val controller = NoopCoroutineDispatcherFactory
      val semaphore = Semaphore(concurrency)
      specs.forEach { ref ->
         log { "DefaultTestSuiteScheduler: Scheduling coroutine for spec [$ref]" }
         launch {
            semaphore.withPermit {
               log { "DefaultTestSuiteScheduler: Acquired permit for $ref" }
               try {
                  val executor = SpecExecutor(listener, controller)
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
