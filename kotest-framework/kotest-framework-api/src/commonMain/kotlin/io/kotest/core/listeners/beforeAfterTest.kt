package io.kotest.core.listeners

import io.kotest.common.SoftDeprecated
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

@SoftDeprecated("Use beforeAny")
interface BeforeTestListener : Listener {

   /**
    * This callback will be invoked before a [TestCase] is executed.
    *
    * If a test case is inactive (disabled), then this method will not
    * be invoked for that particular test case.
    *
    * @param testCase the [TestCase] about to be executed.
    */
   @SoftDeprecated("Use beforeAny")
   suspend fun beforeTest(testCase: TestCase): Unit = Unit

   /**
    * Alias for beforeTest
    */
   suspend fun beforeAny(testCase: TestCase): Unit = Unit
}

@SoftDeprecated("Use afterContainer, afterEach, or afterAny")
interface AfterTestListener : Listener {

   /**
    * This callback is invoked after a [TestCase] has finished.
    *
    * If a test case was skipped (ignored / disabled / inactive) then
    * this callback will not be invoked for that particular test case.
    *
    * @param testCase the [TestCase] that has completed.
    */
   @SoftDeprecated("Use afterAny")
   suspend fun afterTest(testCase: TestCase, result: TestResult): Unit = Unit

   /**
    * Alias for [afterTest]
    */
   suspend fun afterAny(testCase: TestCase, result: TestResult): Unit = Unit
}
