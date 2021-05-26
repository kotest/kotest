package io.kotest.core.listeners

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

interface BeforeTestListener : Listener {

   /**
    * This callback will be invoked before a [TestCase] is executed.
    *
    * If a test case is inactive (disabled), then this method will not
    * be invoked for that particular test case.
    *
    * @param testCase the [TestCase] about to be executed.
    */
   @Deprecated("Use beforeAny. Deprecated since 5.0")
   suspend fun beforeTest(testCase: TestCase): Unit = Unit

   /**
    * Alias for beforeTest
    */
   suspend fun beforeAny(testCase: TestCase): Unit = Unit
}

@Deprecated("Use beforeContainer, beforeEach, or beforeAny. Deprecated since 5.0")
interface AfterTestListener : Listener {

   /**
    * This callback is invoked after a [TestCase] has finished.
    *
    * If a test case was skipped (ignored / disabled / inactive) then
    * this callback will not be invoked for that particular test case.
    *
    * @param testCase the [TestCase] that has completed.
    */
   @Deprecated("Use afterAny. Deprecated since 5.0")
   suspend fun afterTest(testCase: TestCase, result: TestResult): Unit = Unit

   /**
    * Alias for afterTest
    */
   suspend fun afterAny(testCase: TestCase, result: TestResult): Unit = Unit
}
