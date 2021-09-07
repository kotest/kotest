package io.kotest.engine

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.test.TestCaseExecutionListener

/**
 * A [TestCaseExecutionListener] that delegates events to the externally defined
 * mocha/karma/jasmine test methods.
 */
object MochaTestCaseExecutionListener : TestCaseExecutionListener {

   private var promise: dynamic = null

   override suspend fun testStarted(testCase: TestCase) {
      val test = it(testCase.description.name.displayName) { done ->
         promise = done
         Unit
      }
      // some frameworks default to a 2000 timeout,
      // here we set to a high number and use the timeout support kotest provides via coroutines
      test.timeout(Int.MAX_VALUE)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      promise(result.error)
   }

   override suspend fun testIgnored(testCase: TestCase) {
      xit(testCase.displayName) { }
   }
}
