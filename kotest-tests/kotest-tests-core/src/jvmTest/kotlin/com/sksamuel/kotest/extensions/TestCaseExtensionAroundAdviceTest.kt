package com.sksamuel.kotest.extensions

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.test.toTestResult

private const val reason = "this test is skipped cause it's skipped"

// this tests that we can manipulate the result of a test case from an extension
class TestCaseExtensionAroundAdviceTest : StringSpec() {
   object MyExt : TestCaseExtension {
      override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
         return when (testCase.descriptor.name.name) {
            "test1" -> TestResult.Ignored
            "test2" ->
               when (execute(testCase).status) {
                  TestStatus.Error -> TestResult.success(0)
                  else -> AssertionError("boom").toTestResult(0)
               }
            "test3" -> if (testCase.config.enabled) throw RuntimeException() else execute(testCase)
            "test4" -> execute(testCase.copy(config = testCase.config.copy(enabled = false)))
            "test5" -> {
               val enabled = testCase.config.enabledOrReasonIf(testCase)
               if (enabled.isEnabled || enabled.reason != reason) throw RuntimeException() else execute(testCase)
            }
            else -> execute(testCase)
         }
      }
   }

   override fun extensions() = listOf(MyExt)

   init {

      // this exception should not be thrown as the extension will skip evaluation of the test
      "test1" {
         throw RuntimeException()
      }
      // this exception will be thrown but then the test extension will override the failed result to return a success
      "test2" {
         throw RuntimeException()
      }
      // the config for this test should be carried through to the extension
      "test3".config(enabled = false) {
      }
      //  config for this test should be overriden so that the test is actually disabled, and therefore the exception will not be thrown
      "test4".config(enabled = true) {
         throw RuntimeException()
      }
      "test6".config(enabledOrReasonIf = { Enabled.disabled(reason) }) {} // the extension should throw if this test is enabled}
   }
}
