package com.sksamuel.kotest.specs.annotation

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf

class AnnotationSpecFailureTest : AnnotationSpec() {
   class FooException(override val message: String) : Exception()

   private val thrownException = FooException("thrown exception")

   @Test
   fun foo() {
      throw thrownException
   }

   override fun extensions() = listOf(ExceptionCaptureExtension())

   inner class ExceptionCaptureExtension : TestCaseExtension {

      override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
         val result = execute(testCase)
         result.error!! shouldHaveMessage  "thrown exception"
         result.error.shouldBeInstanceOf<FooException>()
         return TestResult.success(0)
      }
   }
}
