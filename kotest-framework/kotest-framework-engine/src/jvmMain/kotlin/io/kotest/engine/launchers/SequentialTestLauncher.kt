package io.kotest.engine.launchers

import io.kotest.core.test.TestCase
import io.kotest.engine.dispatchers.CoroutineDispatcherFactory
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * This implementation of [TestLauncher] will launch all tests consecutively.
 *
 * @param factory a [CoroutineDispatcherFactory] used to allocate dispatchers for tests.
 */
class SequentialTestLauncher(private val factory: CoroutineDispatcherFactory) : TestLauncher {
   override suspend fun launch(run: suspend (TestCase) -> Unit, tests: List<TestCase>) {
      log("SequentialTestLauncher: Launching ${tests.size} sequentially")
      tests.forEach { test ->
         coroutineScope { // will wait for the launched test to complete
            launch(factory.dispatcherFor(test)) {
               run(test)
            }
         }
      }
   }
}
