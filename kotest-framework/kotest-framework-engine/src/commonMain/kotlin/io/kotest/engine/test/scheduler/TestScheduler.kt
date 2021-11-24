package io.kotest.engine.test.scheduler

import io.kotest.common.ExperimentalKotest
import io.kotest.core.test.TestCase

/**
 * A [TestScheduler] is responsible for scheduling test cases as requested
 * by a spec executor.
 *
 */
@ExperimentalKotest
internal interface TestScheduler {

   /**
    * Implement this method to invoke the given tests.
    *
    * @param run the function to invoke to execute each test.
    * @param tests the tests to execute
    */
   suspend fun schedule(run: suspend (TestCase) -> Unit, tests: List<TestCase>)
}

