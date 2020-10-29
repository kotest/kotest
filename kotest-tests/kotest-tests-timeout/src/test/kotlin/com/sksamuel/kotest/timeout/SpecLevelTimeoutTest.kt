package com.sksamuel.kotest.timeout

import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType
import io.kotest.engine.toTestResult
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime
import kotlin.time.minutes
import kotlin.time.seconds

@ExperimentalTime
class SpecLevelInvocationTimeoutContainerTest : FreeSpec({

   timeout = 1.minutes.toLongMilliseconds()
   invocationTimeout = 1.seconds.toLongMilliseconds()

   extension(ext)

   "invocation timeouts at the spec level should not be applied to containers" - {
      "suspending inner test".config(invocations = 10) {
         delay(500)
      }
      "blocking inner test".config(invocations = 10) {
         delay(500)
      }
   }
})

/**
 * A Test Case extension that expects each leaf test to fail, and will invert the test result.
 */
private val ext: TestCaseExtensionFn = { (testCase, execute) ->
   val result = execute(testCase)
   if (testCase.type == TestType.Container)
      result
   else
      when (result.status) {
         TestStatus.Failure, TestStatus.Error -> TestResult.success(0)
         else -> AssertionError("${testCase.description.name.name} passed but should fail").toTestResult(0)
      }
}
