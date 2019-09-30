package com.sksamuel.kotest.assertions

import io.kotest.AssertionMode
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.TestStatus
import io.kotest.assertions.AssertionCounter
import io.kotest.extensions.TestCaseExtension
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.shouldBe
import io.kotest.shouldThrow
import io.kotest.specs.FunSpec

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
                        TestStatus.Error, TestStatus.Failure -> complete(TestResult.success(it.duration))
                        else -> complete(TestResult.error(RuntimeException("Should have failed"), it.duration))
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
