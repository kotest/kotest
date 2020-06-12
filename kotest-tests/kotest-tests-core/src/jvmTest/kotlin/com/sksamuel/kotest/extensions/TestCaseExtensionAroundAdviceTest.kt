package com.sksamuel.kotest.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestStatus
import java.lang.AssertionError
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

// this tests that we can manipulate the result of a test case from an extension
@ExperimentalTime
class TestCaseExtensionAroundAdviceTest : StringSpec() {

   object MyExt : TestCaseExtension {
      override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
         return when (testCase.description.name.name) {
            "test1" -> TestResult.Ignored
            "test2" ->
               when (execute(testCase).status) {
                  TestStatus.Error -> TestResult.success(Duration.ZERO)
                  else -> TestResult.throwable(AssertionError("boom"), Duration.ZERO)
               }
            "test3" -> if (testCase.config.enabled) throw RuntimeException() else execute(testCase)
            "test4" -> execute(testCase.copy(config = testCase.config.copy(enabled = false)))
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
   }
}
