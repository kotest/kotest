package com.sksamuel.kotest.core.runtime

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.toTestResult

// tests that using invocations > 1 propagate any error to the reporters
class InvocationThreadErrorTest : FunSpec({

   aroundTest { (testCase, process) ->
      val result = process(testCase)
      when (result.status) {
         TestStatus.Error -> TestResult.success(0)
         else -> toTestResult(RuntimeException("${testCase.displayName} should fail"), 0)
      }
   }

   test("single invocation / single thread should propagate error").config(invocations = 4) {
      error("boom")
   }

   test("multiple invocations / single thread should propagate error").config(invocations = 5, threads = 1) {
      error("boom")
   }

   test("multiple invocations / multiple threads should propagate error").config(invocations = 5, threads = 3) {
      error("boom")
   }
})
