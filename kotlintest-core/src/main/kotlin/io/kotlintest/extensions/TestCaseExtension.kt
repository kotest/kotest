package io.kotlintest.extensions

import io.kotlintest.TestCase

/**
 * Reusable extension to be registered project wide with
 * [io.kotlintest.AbstractProjectConfig.testCaseExtensions] or on a
 * single spec by overriding `testCaseExtensions()` in
 * the spec class.
 */
interface TestCaseExtension {
  fun intercept(testCase: TestCase, test: () -> Unit)
}