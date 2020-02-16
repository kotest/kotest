package com.sksamuel.kotest.core.runtime

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
class InvocationThreadErrorTest : FunSpec({

   aroundTest { testCase, f ->
      val result = f(testCase)
      when (result.status) {
         TestStatus.Error -> TestResult.success(Duration.ZERO)
         else -> TestResult.throwable(RuntimeException("should fail"), Duration.ZERO)
      }
   }

   test("multiple invocations should propogate error").config(invocations = 4) {
      error("boom")
   }

   test("multiple threads should propogate error").config(invocations = 4, threads = 3) {
      error("boom")
   }
})
