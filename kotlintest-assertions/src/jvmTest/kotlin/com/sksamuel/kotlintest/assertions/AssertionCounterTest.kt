package com.sksamuel.kotlintest.assertions

import io.kotlintest.AssertionMode
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.assertions.AssertionCounter
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec

class AssertionCounterTest : FunSpec() {

   override fun assertionMode() = AssertionMode.Error

   override fun extensions(): List<TestCaseExtension> = listOf(
      object : TestCaseExtension {
         override suspend fun intercept(testCase: TestCase,
                                        execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
                                        complete: suspend (TestResult) -> Unit) {
            when (testCase.name) {
               "AssertionMode.Error assertion mode should fail the test if no assertions were present" -> {
                  execute(testCase) {
                     when (it.status) {
                        TestStatus.Error, TestStatus.Failure -> complete(TestResult.success(it.durationMs))
                        else -> complete(TestResult.error(RuntimeException("Should have failed"), it.durationMs))
                     }
                  }
               }
               else -> execute(testCase) { complete(it) }
            }
         }
      }
   )

   init {

      test("assertion counter should count number of assertions") {
         // using the shouldBe here at the start will also increase the count
         AssertionCounter.get() shouldBe 0
         1 shouldBe 1
         "hello".shouldHaveLength(5)
         AssertionCounter.get() shouldBe 3
      }

      test("AssertionMode.Error assertion mode should fail the test if no assertions were present") {

      }

      test("assertion counter should be reset between tests") {
         AssertionCounter.get() shouldBe 0
      }

      test("testing for throwable should count towards assertion total") {
         shouldThrow<RuntimeException> {
            throw RuntimeException("shazzam")
         }
      }
   }
}
