package io.kotlintest.runner.jvm

import io.kotlintest.TestResult
import io.kotlintest.Scope

/**
 * Implementations of this interface will be notified of events
 * that occur as part of the test runner lifecycle.
 */
interface TestRunnerListener {

  fun executionStarted()
  fun executionFinished(t: Throwable?)

  fun executionStarted(scope: Scope)
  fun executionFinished(scope: Scope, result: TestResult)
}