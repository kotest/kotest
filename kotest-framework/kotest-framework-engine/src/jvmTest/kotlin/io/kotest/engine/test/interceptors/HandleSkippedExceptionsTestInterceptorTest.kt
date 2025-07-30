package io.kotest.engine.test.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.test.runIf
import io.kotest.engine.IterationSkippedException
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.scopes.NoopTestScope
import io.kotest.matchers.shouldBe
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class HandleSkippedExceptionsTestInterceptorTest : FunSpec({

   test("test should be ignored when runIf block returns false") {
      runIf { false }
      error("boom")
   }

   test("HandleSkippedExceptionsTestInterceptor should adjust TestAbortedException to Ignored") {
      val tc = TestCase(
         HandleSkippedExceptionsTestInterceptorTest::class.toDescriptor().append("a"),
         TestNameBuilder.builder("a").build(),
         HandleSkippedExceptionsTestInterceptorTest(),
         {},
         SourceRef.None,
         TestType.Test,
      )
      HandleSkippedExceptionsTestInterceptor.intercept(tc, NoopTestScope(tc, coroutineContext)) { _, _ ->
         TestResult.Error(1.seconds, IterationSkippedException())
      }.isIgnored shouldBe true
   }

   test("HandleSkippedExceptionsTestInterceptor should adjust IterationSkippedException to Ignored") {
      val tc = TestCase(
         HandleSkippedExceptionsTestInterceptorTest::class.toDescriptor().append("a"),
         TestNameBuilder.builder("a").build(),
         HandleSkippedExceptionsTestInterceptorTest(),
         {},
         SourceRef.None,
         TestType.Test,
      )
      HandleSkippedExceptionsTestInterceptor.intercept(tc, NoopTestScope(tc, coroutineContext)) { _, _ ->
         TestResult.Error(1.seconds, IterationSkippedException())
      }.isIgnored shouldBe true
   }

   test("HandleSkippedExceptionsTestInterceptor should not adjust exceptions that are not handled") {
      val tc = TestCase(
         HandleSkippedExceptionsTestInterceptorTest::class.toDescriptor().append("a"),
         TestNameBuilder.builder("a").build(),
         HandleSkippedExceptionsTestInterceptorTest(),
         {},
         SourceRef.None,
         TestType.Test,
      )
      HandleSkippedExceptionsTestInterceptor.intercept(tc, NoopTestScope(tc, coroutineContext)) { _, _ ->
         TestResult.Error(1.seconds, IOException())
      }.isError shouldBe true
   }

   test("HandleSkippedExceptionsTestInterceptor should not adjust AssertionErrors") {
      val tc = TestCase(
         HandleSkippedExceptionsTestInterceptorTest::class.toDescriptor().append("a"),
         TestNameBuilder.builder("a").build(),
         HandleSkippedExceptionsTestInterceptorTest(),
         {},
         SourceRef.None,
         TestType.Test,
      )
      HandleSkippedExceptionsTestInterceptor.intercept(tc, NoopTestScope(tc, coroutineContext)) { _, _ ->
         TestResult.Error(1.seconds, AssertionError())
      }.isError shouldBe true
   }
})
