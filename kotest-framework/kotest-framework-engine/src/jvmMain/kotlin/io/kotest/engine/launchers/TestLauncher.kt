package io.kotest.engine.launchers

import io.kotest.core.test.TestCase

/**
 * A [TestLauncher] is responsible for launching a [TestCase] into a coroutine.
 *
 */
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
