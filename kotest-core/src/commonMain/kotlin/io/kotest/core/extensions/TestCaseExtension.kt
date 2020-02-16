package io.kotest.core.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Reusable extension that intercepts calls to a [TestCase].
 *
 * These extensions can be registered project wide using
 * [AbstractProjectConfig.extensions], or on a per-spec basis
 * by setting `extensions()` in a [Spec] class, or finally
 * on individual tests themselves via [TestCaseConfig].
 */
interface TestCaseExtension : Extension {

   /**
    * Intercepts a [TestCase].
    *
    * Allows implementations to add logic around a [TestCase] as well as
    * control if (or when) the test is executed.
    *
    * The supplied `execute` function should be invoked if implementations wish to
    * execute the test case.
    *
    * This function requires the actual test case instance that will be executed,
    * which is normally the same instance supplied, but can in fact be another.
    * This allows you to change config for example:
    *
    * `execute(testCase.copy(config = ...))`
    *
    * The execute function additionally expects a callback function which will be invoked
    * with the [TestResult] after the test has completed.
    *
    * Implementations must invoke the `complete` function with a [TestResult]
    * which is used to notify the underlying platform (eg, junit5)
    * as to the outcome of the test.
    *
    * Typically the result recieved in the callback will be then used to invoke
    * the complete function but this is not required. Implementations may wish to
    * skip or abort the test case and invoke complete without ever running the test.
    *
    * Or they may wish to run the test and then invoke complete with a different
    * result to that received.
    *
    * Note: !!Failure to invoke the complete function will result in the test runnner
    * waiting indefinitely for the outcome of the test case!!
    *
    * Note: A test cannot be skipped after it has been executed. Executing a test
    * and then returning [TestStatus.Ignored] can result in errors on the JUnit platform.
    *
    * @param testCase the [TestCase] under interception - contains the [Spec]
    * instance containing this test case, the [TestCaseConfig] to be passed to the test
    * and a [Description] which contains the id and parent ids of the test case.
    *
    * @param execute a function that is invoked to execute the test. Can be ignored if you
    * wish to return a result without executing the test itself.
    *
    * @param complete a function that must be invoked with a [TestResult] to
    * notify the test runner of the outcome of the test.
    */
   suspend fun intercept(
      testCase: TestCase,
      execute: suspend (TestCase) -> TestResult
   ): TestResult
}
