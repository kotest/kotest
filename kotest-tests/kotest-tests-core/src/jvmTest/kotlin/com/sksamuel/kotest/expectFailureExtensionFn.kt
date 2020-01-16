package com.sksamuel.kotest

import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
val expectFailureExtension: TestCaseExtensionFn = { testCase, execute, complete ->
   execute(testCase) { result ->
      when (result.status) {
         TestStatus.Failure, TestStatus.Error -> complete(
             TestResult.success(Duration.ZERO)
         )
         else -> complete(
             TestResult.failure(
                 AssertionError("Should not happen"),
                 Duration.ZERO
             )
         )
      }
   }
}
