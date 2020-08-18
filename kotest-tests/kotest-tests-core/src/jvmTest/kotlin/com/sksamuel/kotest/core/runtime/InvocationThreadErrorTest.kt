package com.sksamuel.kotest.core.runtime

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.toTestResult

class InvocationThreadErrorTest : FunSpec({

   aroundTest { (testCase, process) ->
      val result = process(testCase)
      when (result.status) {
         TestStatus.Error -> TestResult.success(0)
         else -> toTestResult(RuntimeException("should fail"), 0)
      }
   }

   test("multiple invocations should propogate error").config(invocations = 4) {
      error("boom")
   }

   test("multiple threads should propogate error").config(invocations = 4, threads = 3) {
      error("boom")
   }
})
