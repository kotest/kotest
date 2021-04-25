package io.kotest.engine.launchers

import io.kotest.core.config.Configuration
import io.kotest.common.ExperimentalKotest
import io.kotest.core.internal.resolvedConcurrentTests
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.dispatchers.coroutineDispatcherFactory
import kotlin.math.max

/**
 * A [TestLauncher] is responsible for launching a [TestCase] into a coroutine.
 *
 */
@ExperimentalKotest
interface TestLauncher {

   /**
    * Implement this method to invoke the given tests.
    *
    * @param run the function to invoke to execute the test. This function would typically run
    * inside it's own coroutine.
    *
    * @param tests the tests to execute
    */
   suspend fun launch(run: suspend (TestCase) -> Unit, tests: List<TestCase>)
}

@ExperimentalKotest
fun testLauncher(spec: Spec): TestLauncher {
   val factory = coroutineDispatcherFactory()
   return when (val concurrentTests = spec.resolvedConcurrentTests()) {
      Configuration.Sequential -> SequentialTestLauncher(factory)
      else -> ConcurrentTestLauncher(max(1, concurrentTests), factory)
   }
}
