package com.sksamuel.kotest.coroutines

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
class CoroutineExceptionTest : FunSpec({

   extension { testCase, execute, complete ->
      execute(testCase) { result ->
         when (result.status) {
            TestStatus.Failure, TestStatus.Error -> complete(TestResult.success(Duration.ZERO))
            else -> complete(TestResult.failure(AssertionError("Should not happen"), Duration.ZERO))
         }
      }
   }

   test("exception in coroutine") {
      launch {
         error("boom")
      }
   }
})
