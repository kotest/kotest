package io.kotest.engine.test.scheduler

import io.kotest.common.ExperimentalKotest
import io.kotest.core.test.TestCase
import io.kotest.core.log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * This implementation of [TestScheduler] will launch all tests consecutively, waiting
 * for each test to complete before launching the next.
 */
@ExperimentalKotest
internal object SequentialTestScheduler : TestScheduler {
   override suspend fun schedule(run: suspend (TestCase) -> Unit, tests: List<TestCase>) {
      log { "SequentialTestLauncher: Launching ${tests.size} sequentially" }
      tests.forEach { test ->
         coroutineScope { // will wait for the launched test to complete
            launch {
               run(test)
            }
         }
      }
   }
}
