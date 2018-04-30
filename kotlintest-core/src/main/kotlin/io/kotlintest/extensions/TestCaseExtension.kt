package io.kotlintest.extensions

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestScope
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult

/**
 * Reusable test case extension to be registered project wide
 * using [AbstractProjectConfig.extensions], on a per-spec
 * single spec by overriding `extensions()` in a [Spec] class,
 * or via [TestCaseConfig]
 */
interface TestCaseExtension : ProjectLevelExtension, SpecLevelExtension {

  /**
   * Intercepts a [TestScope].
   *
   * Allows implementations to add logic around a test case as well as control
   * if and when the test case is executed.
   *
   * The supplied `test` function should be invoked if implementations wish to
   * execute the test case. The function accepts a config parameter which is the
   * [TestCaseConfig] to be used when executing the test. The [TestCaseInterceptContext]
   * contains the config supplied by the author of the test, or it may contain the
   * config supplied by another interceptor.
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
   * Failure to invoke the complete function will result in the test runnner
   * waiting indefinitely for the outcome of the test case.
   *
   * @param context details of the [TestScope] under interception - contains the [Spec]
   * instance containing the [TestScope], the [TestCaseConfig] to be passed to the test
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
                complete: (TestResult) -> Unit) = test(context.config, { complete(it) })
}

data class TestCaseInterceptContext(val description: Description,
                                    val spec: Spec,
                                    val config: TestCaseConfig)