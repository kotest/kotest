package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.interceptors.MarkAbortedExceptionsAsSkippedTestInterceptor
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.result.shouldBeSuccess
import org.opentest4j.TestAbortedException
import kotlin.time.Duration.Companion.milliseconds

class AbortedExceptionTest : FreeSpec({

   val fakeTestCase = TestCase(
      Descriptor.TestDescriptor(
         Descriptor.SpecDescriptor(DescriptorId("dummy"), DummySpec::class),
         DescriptorId("test")
      ), TestName("dummy"), DummySpec(), {}, type = TestType.Test
   )

   "Test should be marked as Ignored" {
      val result = MarkAbortedExceptionsAsSkippedTestInterceptor.intercept(DummySpec()) {
         Result.success(
            mapOf(fakeTestCase to TestResult.Error(1.milliseconds, TestAbortedException()))
         )
      }

      result.shouldBeSuccess()
         .values
         .shouldContainExactly(TestResult.Ignored)
   }

   "Failure is not reclassified" {
      val assertionError = AssertionError("blah")
      val result = MarkAbortedExceptionsAsSkippedTestInterceptor.intercept(DummySpec()) {
         Result.success(
            mapOf(fakeTestCase to TestResult.Failure(1.milliseconds, assertionError))
         )
      }

      result.shouldBeSuccess()
         .values
         .shouldContainExactly(TestResult.Failure(1.milliseconds, assertionError))
   }

   "Successful test is not reclassified" {
      val result = MarkAbortedExceptionsAsSkippedTestInterceptor.intercept(DummySpec()) {
         Result.success(
            mapOf(fakeTestCase to TestResult.Success(1.milliseconds))
         )
      }

      result.shouldBeSuccess()
         .values
         .shouldContainExactly(TestResult.Success(1.milliseconds))
   }
})

private class DummySpec : FreeSpec()

