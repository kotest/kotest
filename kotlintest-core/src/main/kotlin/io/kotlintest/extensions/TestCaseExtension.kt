package io.kotlintest.extensions

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult

/**
 * Reusable test case extension to be registered project wide
 * using [AbstractProjectConfig.extensions] or on a per-spec
 * single spec by overriding `extensions()` in a [Spec] class.
 */
interface TestCaseExtension : Extension {

  /**
   * Intercepts a [TestCase].
   *
   * Implementations must return a [TestResult] which is used to notify
   * the underlying platform (eg, junit5) as to the outcome of the test.
   *
   * Typically, implementations will invoke the test function to execute
   * the [TestCase], and return that value. However after the test has finished, you can
   * discard that result and return an alternative if required.
   *
   * The test function accepts a config parameter which is the [TestCaseConfig] used to execute
   * the test. Interception functions are able to modify this, opening up the possibility for
   * dynamic config.
   *
   * Alternatively, implementations can bypass the test entirely and return a failed, errored
   * or skipped result.
   *
   * @param description full name of the [TestCase] under interception
   * @param spec the [Spec] instance containing the [TestCase]
   * @param config the [TestCaseConfig] defined on the [TestCase]
   * @param test a function that is invoked to execute the test.
   * Can be ignored if you wish to return a [TestResult] without executing the test.
   *
   * @return a [TestResult] for the desired outcome of this test case.
   */
  fun intercept(description: Description,
                spec: Spec,
                config: TestCaseConfig,
                test: (TestCaseConfig) -> TestResult): TestResult = test(config)
}

interface TestCaseContinuation {
  fun run(): TestResult
  fun skip()
  fun abort(t: Throwable)
}