package io.kotest.core.spec

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Defines functions which can be overriden to register callbacks.
 * This is an alternative style to using [InlineCallbacks].
 */
interface FunctionCallbacks {

   fun beforeSpec(spec: Spec) {}

   fun afterSpec(spec: Spec) {}

   fun beforeTest(testCase: TestCase) {}

   fun afterTest(testCase: TestCase, result: TestResult) {}

   fun beforeContainer(testCase: TestCase) {}

   fun afterContainer(testCase: TestCase, result: TestResult) {}

   fun beforeEach(testCase: TestCase) {}

   fun afterEach(testCase: TestCase, result: TestResult) {}

   fun beforeAny(testCase: TestCase) {}

   fun afterAny(testCase: TestCase, result: TestResult) {}
}
