package com.sksamuel.kotest

import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.test.TestResult
import io.kotest.engine.test.toTestResult

val expectFailureExtension: TestCaseExtensionFn = {
   val result = it.b(it.a)
   when (result.status) {
      TestStatus.Failure, TestStatus.Error -> TestResult.success(0)
      else -> AssertionError("Should not happen").toTestResult(0)
   }
}
