package io.kotest.engine.test.interceptors

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.ExpectFailureException
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.runIf
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.scopes.NoopTestScope
import io.kotest.matchers.shouldBe
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

class ExpectExceptionTestInterceptorTest : FunSpec({

   test("test should be ignored when runIf block returns false") {
      runIf { false }
      error("boom")
   }

   test("ExpectExceptionTestInterceptor should adjust ExpectFailureException to Ignored") {
      val tc = TestCase(
         ExpectExceptionTestInterceptorTest::class.toDescriptor().append("a"),
         TestNameBuilder.builder("a").build(),
         ExpectExceptionTestInterceptorTest(),
         {},
         SourceRef.None,
         TestType.Test,
      )
      ExpectExceptionTestInterceptor.intercept(tc, NoopTestScope(tc, coroutineContext)) { _, _ ->
         TestResult.Error(1.seconds, ExpectFailureException)
      }.isIgnored shouldBe true
   }

   test("ExpectExceptionTestInterceptor should not adjust exceptions that are not ExpectFailureException") {
      val tc = TestCase(
         ExpectExceptionTestInterceptorTest::class.toDescriptor().append("a"),
         TestNameBuilder.builder("a").build(),
         ExpectExceptionTestInterceptorTest(),
         {},
         SourceRef.None,
         TestType.Test,
      )
      ExpectExceptionTestInterceptor.intercept(tc, NoopTestScope(tc, coroutineContext)) { _, _ ->
         TestResult.Error(1.seconds, IOException())
      }.isError shouldBe true
   }

   test("ExpectExceptionTestInterceptor should not adjust AssertionErrors") {
      val tc = TestCase(
         ExpectExceptionTestInterceptorTest::class.toDescriptor().append("a"),
         TestNameBuilder.builder("a").build(),
         ExpectExceptionTestInterceptorTest(),
         {},
         SourceRef.None,
         TestType.Test,
      )
      ExpectExceptionTestInterceptor.intercept(tc, NoopTestScope(tc, coroutineContext)) { _, _ ->
         TestResult.Error(1.seconds, AssertionError())
      }.isError shouldBe true
   }
})
