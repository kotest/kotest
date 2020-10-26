package com.sksamuel.kotest.specs.annotation

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe

class AnnotationSpecFailureTest : AnnotationSpec() {
   class FooException : Exception()

   private val thrownException = FooException()

   @Test
   fun foo() {
      throw thrownException
   }

   override fun extensions() = listOf(ExceptionCaptureExtension())

   inner class ExceptionCaptureExtension : TestCaseExtension {

      override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
         val result = execute(testCase)
         result.error shouldBe thrownException
         return TestResult.success(0)
      }
   }
}
