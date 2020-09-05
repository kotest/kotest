package com.sksamuel.kotest.specs.annotation

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.assertions.fail
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.engine.toTestResult
import io.kotest.matchers.shouldBe

class AnnotationSpecTest : AnnotationSpec() {

   private class FooException : RuntimeException()
   private class BarException : RuntimeException()

   private var count = 0

   @Test
   fun test1() {
      count += 1
   }

   @Test // should find private tests too
   private fun test2() {
      count += 1
   }

   @Test
   fun `!bangedTest`() {
      throw FooException()  // Test should pass as this should be banged
   }

   @Test(expected = FooException::class)
   fun test3() {
      throw FooException()  // This test should pass!
   }

   @Test(expected = FooException::class)
   fun test4() {
      throw BarException()
   }

   @Test(expected = FooException::class)
   fun test5() {
      // Throw nothing
   }

   override fun afterSpec(spec: Spec) {
      count shouldBe 2
   }

   override fun extensions(): List<Extension> = listOf(IgnoreFailedTestExtension)

   private object IgnoreFailedTestExtension : TestCaseExtension {

      override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
         if (testCase.displayName !in listOf("test4", "test5")) return execute(testCase)

         val result = execute(testCase)
         if (result.error !is AssertionError) {
            return AssertionError("Expecting an assertion error!").toTestResult(0)
         }

         val errorMessage = result.error!!.message
         val wrongExceptionMessage = "Expected exception of class FooException, but BarException was thrown instead."
         val noExceptionMessage = "Expected exception of class FooException, but no exception was thrown."

         return when (testCase.displayName) {
            "test4" -> if (errorMessage == wrongExceptionMessage) {
               TestResult.success(0)
            } else {
               AssertionError("Wrong message.").toTestResult(0)
            }
            "test5" -> if (errorMessage == noExceptionMessage) {
               TestResult.success(0)
            } else {
               AssertionError("Wrong message.").toTestResult(0)
            }
            else -> fail("boom")
         }
      }
   }
}


