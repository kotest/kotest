package com.sksamuel.kotest.assertions

import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.assertions.assertionCounter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.toTestResult
import io.kotest.matchers.shouldBe

class AssertionCounterFunSpecTest : FunSpec() {

   override fun assertionMode() = AssertionMode.Error

   override fun extensions(): List<TestCaseExtension> = listOf(
      object : TestCaseExtension {
         override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
            return when (testCase.displayName) {
               "AssertionMode.Error assertion mode should fail the test if no assertions were present" -> {
                  val result = execute(testCase)
                  when (result.status) {
                     TestStatus.Error, TestStatus.Failure -> TestResult.success(result.duration)
                     else -> toTestResult(RuntimeException("Should have failed"), result.duration)
                  }
               }
               else -> execute(testCase)
            }
         }
      }
   )

   init {

      test("assertion counter should count number of assertions") {
         // using the shouldBe here at the start will also increase the count
         assertionCounter.get() shouldBe 0
         1 shouldBe 1
         "hello".length shouldBe 5
         assertionCounter.get() shouldBe 3
      }

      test("AssertionMode.Error assertion mode should fail the test if no assertions were present") {

      }

      test("assertion counter should be reset between tests") {
         assertionCounter.get() shouldBe 0
      }

      test("testing for throwable should count towards assertion total") {
         shouldThrow<RuntimeException> {
            throw RuntimeException("shazzam")
         }
      }
   }
}
