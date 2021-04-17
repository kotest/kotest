package io.kotest.engine.launchers

import io.kotest.common.ExperimentalKotest
import io.kotest.core.test.TestCase
import io.kotest.engine.dispatchers.CoroutineDispatcherFactory
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * A [SpecLauncher] that launches tests concurrently.
 *
 * @param maxConcurrent The maximum number of coroutines to launch. Uses a semaphore to limit.
 * @param factory a [CoroutineDispatcherFactory] used to allocate dispatchers to tests.
 */
@ExperimentalKotest
class ConcurrentTestLauncher(
   private val maxConcurrent: Int,
   private val factory: CoroutineDispatcherFactory
) : TestLauncher {

   private val semaphore = Semaphore(maxConcurrent)

   override suspend fun launch(run: suspend (TestCase) -> Unit, tests: List<TestCase>) {
      log("ConcurrentTestLauncher: Launching ${tests.size} tests with $maxConcurrent max concurrency")
      coroutineScope {
         tests.forEach { test ->
            semaphore.withPermit {
               log("ConcurrentTestLauncher: Acquired permit for [$test]")
               val dispatcher = factory.dispatcherFor(test)
               log("ConcurrentTestLauncher: Launching coroutine for test [$test] with dispatcher [$dispatcher]")
               launch(dispatcher) {
                  try {
                     run(test)
                  } catch (t: Throwable) {
                     log("ConcurrentTestLauncher: Unhandled error during test execution [$test] [$t]")
                     throw t
                  }
               }.invokeOnCompletion {
                  log("ConcurrentTestLauncher: Test [$test] has completed")
                  factory.complete(test)
               }
            }
         }
      }
      log("ConcurrentTestLauncher: All tests have completed")
   }
}
