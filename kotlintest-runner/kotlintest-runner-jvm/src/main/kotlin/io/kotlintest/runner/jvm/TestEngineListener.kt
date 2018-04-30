package io.kotlintest.runner.jvm

import io.kotlintest.Spec
import io.kotlintest.TestResult
import io.kotlintest.TestScope

/**
 * Implementations of this interface will be notified of events
 * that occur as part of the [TestEngine] lifecycle.
 */
interface TestEngineListener {

  fun executionStarted()
  fun executionFinished(t: Throwable?)

  fun executionStarted(spec: Spec)
  fun executionFinished(spec: Spec, t: Throwable?)

  fun executionStarted(scope: TestScope)
  fun executionFinished(scope: TestScope, result: TestResult)
}