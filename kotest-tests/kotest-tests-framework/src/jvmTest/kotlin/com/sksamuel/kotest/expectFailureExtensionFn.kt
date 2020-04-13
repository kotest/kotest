package com.sksamuel.kotest

import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val expectFailureExtension: TestCaseExtensionFn = {
   val result = it.b(it.a)
   when (result.status) {
      TestStatus.Failure, TestStatus.Error -> TestResult.success(Duration.ZERO)
      else -> TestResult.throwable(AssertionError("Should not happen"), Duration.ZERO)
   }
}
