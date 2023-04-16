package io.kotest.core.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Extension point that intercepts calls to a [TestCase].
 *
 * This extension can be used to override the results of a test, or whether a test is invoked.
 *
 */
interface TestCaseExtension : Extension {

   /**
    * Intercepts a [TestCase], returning the result of the execution.
    *
    * Allows implementations to add logic around a [TestCase] as well as
    * control if (or when) the test is executed.
    *
    * The supplied [execute] function should be invoked if implementations wish to
    * execute the test case.
    *
    * This function accepts the test case instance to be executed, which is normally
    * the same instance supplied, but can be changed by this method.
    *
    * This allows you to change config for example:
    *
    * ```
    * execute(testCase.copy(config = ...))
    * ```
    * Typically, the result received from invoking [execute] will be the result
    * returned, but this is not required. Implementations may wish to
    * skip the test case and return a result without executing the test, or they
    * may wish to inspect the test and return a different result to that received.
    *
    * Note: A test cannot be skipped after it has been executed. Executing a test
    * and then returning [io.kotest.core.test.TestStatus.Ignored] may result in an error.
    *
    * @param testCase the [TestCase] under interception
    *
    * @param execute a function that is invoked to execute the test. Can be ignored if you
    * wish to return a result without executing the test itself.
    */
   suspend fun intercept(
      testCase: TestCase,
      execute: suspend (TestCase) -> TestResult
   ): TestResult
}
