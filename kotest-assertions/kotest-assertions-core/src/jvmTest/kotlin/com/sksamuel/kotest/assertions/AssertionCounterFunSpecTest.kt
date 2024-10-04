package com.sksamuel.kotest.assertions

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class AssertionCounterFunSpecTest : FunSpec() {

   override fun assertionMode() = AssertionMode.Error

   override fun extensions(): List<Extension> = listOf(
      object : TestCaseExtension {
         override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
            return when (testCase.name.testName) {
               "AssertionMode.Error assertion mode should fail the test if no assertions were present" -> {
                  when (val result = execute(testCase)) {
                     is TestResult.Error, is TestResult.Failure -> TestResult.Success(result.duration)
                     else -> TestResult.Error(
                        result.duration,
                        RuntimeException("Should have failed: ${testCase.name.testName}")
                     )
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
         assertionCounter.get() shouldBe 1
      }
   }
}
