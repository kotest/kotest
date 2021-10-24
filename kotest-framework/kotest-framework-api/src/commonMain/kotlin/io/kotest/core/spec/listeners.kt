package io.kotest.core.spec

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Returns a [TestListener] which passes lifecycle events through to the appropriate
 * spec member function.
 */
fun Spec.functionOverrideCallbacks() = object : TestListener {
   override suspend fun afterSpec(spec: Spec) {
      this@functionOverrideCallbacks.afterSpec(spec)
   }

   override suspend fun beforeSpec(spec: Spec) {
      this@functionOverrideCallbacks.beforeSpec(spec)
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      this@functionOverrideCallbacks.afterTest(testCase, result)
   }

   override suspend fun beforeTest(testCase: TestCase) {
      this@functionOverrideCallbacks.beforeTest(testCase)
   }

   override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
      this@functionOverrideCallbacks.afterContainer(testCase, result)
   }

   override suspend fun beforeContainer(testCase: TestCase) {
      this@functionOverrideCallbacks.beforeContainer(testCase)
   }

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      this@functionOverrideCallbacks.afterEach(testCase, result)
   }

   override suspend fun beforeEach(testCase: TestCase) {
      this@functionOverrideCallbacks.beforeEach(testCase)
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      this@functionOverrideCallbacks.afterAny(testCase, result)
   }

   override suspend fun beforeAny(testCase: TestCase) {
      this@functionOverrideCallbacks.beforeAny(testCase)
   }
}
