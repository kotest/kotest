package com.sksamuel.kotest

import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
val expectFailureExtension: TestCaseExtensionFn = { testCase, execute ->
   val result = execute(testCase)
   when (result.status) {
      TestStatus.Failure, TestStatus.Error -> TestResult.success(Duration.ZERO)
      else -> TestResult.failure(AssertionError("Should not happen"), Duration.ZERO)
   }
}
