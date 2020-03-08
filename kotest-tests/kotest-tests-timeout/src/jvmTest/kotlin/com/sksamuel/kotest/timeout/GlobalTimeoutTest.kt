package com.sksamuel.kotest.timeout

import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class GlobalTimeoutTest : StringSpec() {

   init {

      "a blocked thread should timeout if global timeout is applied" {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         Thread.sleep(1000000)
      }

      "a suspended coroutine should timeout if a global timeout is applied" {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         delay(1000000)
      }

      extension(expectFailureExtension)
   }
}

@OptIn(ExperimentalTime::class)
val expectFailureExtension: TestCaseExtensionFn = { (testCase, execute) ->
   val result = execute(testCase)
   when (result.status) {
      TestStatus.Failure, TestStatus.Error -> TestResult.success(Duration.ZERO)
      else -> TestResult.throwable(AssertionError("Should not happen"), Duration.ZERO)
   }
}
