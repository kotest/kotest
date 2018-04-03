package io.kotlintest.extensions

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.Spec
import io.kotlintest.TestCase

/**
 * Reusable test case extension to be registered project wide
 * using [AbstractProjectConfig.extensions] or on a per-spec
 * single spec by overriding `extensions()` in a [Spec] class.
 */
interface TestCaseExtension : Extension {

  /**
   * You must invoke test() otherwise the spec will
   * not be executed.
   */
  @Deprecated("This interceptor function is deprecated, please consider using a TestListener")
  fun intercept(testCase: TestCase, test: () -> Unit)
}