package io.kotest.core.spec

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Returns the resolved listeners for a given [Spec].
 *
 * This is, listeners returned from the `listener` function override, listeners assigned via
 * the inline function, listeners generated from the lambda functions, and listeners
 * from the specific function overrides.
 */
fun Spec.resolvedTestListeners(): List<TestListener> {
   return listeners() + // listeners defined by overriding the listeners function
      this.registeredListeners() + // listeners added via the inline callbacks
      this.functionOverrideCallbacks() // listeners from the overrides
}

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
