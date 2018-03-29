package io.kotlintest.extensions

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.TestCase

/**
 * Reusable extension to be registered project wide with
 * [AbstractProjectConfig.extensions] or on a
 * single spec by overriding `testCaseExtensions()` in
 * the spec class.
 */
interface TestCaseExtension : Extension {
  @Deprecated("This interceptor function is deprecated, please consider using a TestListener")
  fun intercept(testCase: TestCase, test: () -> Unit)
}