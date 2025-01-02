package io.kotest.engine.test.scheduler

import io.kotest.common.ExperimentalKotest
import io.kotest.core.test.TestCase
import io.kotest.core.log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * A [TestScheduler] that launches tests concurrently up to a maximum limit.
 *
 * @param maxConcurrent The maximum number of tests to schedule concurrently.
 */
@ExperimentalKotest
internal class ConcurrentTestScheduler(private val maxConcurrent: Int) : TestScheduler {

   private val semaphore = Semaphore(maxConcurrent)

   override suspend fun schedule(run: suspend (TestCase) -> Unit, tests: List<TestCase>) {
      log { "ConcurrentTestScheduler: Launching ${tests.size} tests with $maxConcurrent max concurrency" }
      coroutineScope { // will wait for all tests to complete
         tests.forEach { test ->
            log { "ConcurrentTestScheduler: Launching coroutine for test [$test]" }
            launch {
               semaphore.withPermit {
                  log { "ConcurrentTestScheduler: Acquired permit for [$test]" }
                  try {
                     run(test)
                  } catch (t: Throwable) {
                     log { "ConcurrentTestScheduler: Unhandled error during test execution [$test] [$t]" }
                     throw t
                  }
               }
            }.invokeOnCompletion {
               log { "ConcurrentTestScheduler: Test [$test] has completed" }
            }
         }
      }
      log { "ConcurrentTestScheduler: All tests have completed" }
   }
}
