package io.kotlintest.extensions

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.TestCase
import io.kotlintest.TestStatus

/**
 * Reusable extension that intercepts calls to a [TestCase].
 *
 * These extensions can be registered project wide using
 * [AbstractProjectConfig.extensions], or on a per-spec basis
 * by overriding `extensions()` in a [Spec] class, or finally
 * on individual tests themselves via [TestCaseConfig].
 */
interface TestCaseExtension : ProjectLevelExtension, SpecLevelExtension {

  /**
   * Intercepts a [TestCase].
   *
   * Allows implementations to add logic around a [TestCase] as well as
   * control if (or when) the scope is executed.
   *
   * The supplied `test` function should be invoked if implementations wish to
   * execute the test case. The function accepts a config parameter which is the
   * [TestCaseConfig] to be used when executing the test. The [TestCaseInterceptContext]
   * contains the config supplied by the author of the test, or it may contain an
   * overriden config supplied by an earlier interceptor.
   *
   * The test function additionally expects a callback function which will be invoked
   * with the [TestResult] returned when executing the test case.
   *
   * Implementations must invoke the `complete` function with a [TestResult]
   * which is used to notify the underlying platform (eg, junit5)
   * as to the outcome of the test.
   *
   * Typically the test result recieved in the callback will be used to call
   * the complete function but this is not required. Implementations may wish to
   * skip or abort the test case and invoke complete without ever running the test.
   * Or they may wish to run the test and then invoke complete with a different
   * result to that received.
   *
   * Note: Failure to invoke the complete function will result in the test runnner
   * waiting indefinitely for the outcome of the test case.
   *
   * Note: A test cannot be skipped after it has been executed. Executing a test
   * and then returning [TestStatus.Ignored] can result in errors on the JUnit platform.
   *
   * @param context details of the [TestCase] under interception - contains the [Spec]
   * instance containing the [TestCase], the [TestCaseConfig] to be passed to the test
   * and a [Description] which contains the id and parent ids of the test case.
   *
   * @param test a function that is invoked to execute the test. Can be ignored if you
   * wish to return a [TestResult] without executing the test itself.
   *
   * @param complete a function that must be invoked with a [TestResult] to
   * notify the test runner of the outcome of the test.
   *
   * @return a [TestResult] for the desired outcome of this test case.
   */
  fun intercept(context: TestCaseInterceptContext,
                test: (TestCaseConfig, (TestResult) -> Unit) -> Unit,
                complete: (TestResult) -> Unit) = test(context.config) { complete(it) }
}

data class TestCaseInterceptContext(val description: Description,
                                    val spec: Spec,
                                    val config: TestCaseConfig)