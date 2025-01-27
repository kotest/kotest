package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.interceptors.InvocationCountCheckInterceptor
import io.kotest.engine.test.scopes.NoopTestScope
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxCondition::class)
class InvocationCountCheckInterceptorTest : DescribeSpec() {
   init {
      describe("InvocationCountCheckInterceptor") {

         it("should invoke downstream if invocation count == 1 for containers") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestNameBuilder.builder("foo").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Container,
            )
            var fired = false
            InvocationCountCheckInterceptor(TestConfigResolver()).intercept(
               tc.copy(config = tc.config?.copy(invocations = 1)),
               NoopTestScope(tc, coroutineContext)
            ) { _, _ ->
               fired = true
               TestResult.Success(0.milliseconds)
            }
            fired.shouldBeTrue()
         }

         it("should invoke downstream if invocation count > 1 for tests") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestNameBuilder.builder("foo").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Test,
            )
            var fired = false
            InvocationCountCheckInterceptor(TestConfigResolver()).intercept(
               tc.copy(config = tc.config?.copy(invocations = 44)),
               NoopTestScope(tc, coroutineContext)
            ) { _, _ ->
               fired = true
               TestResult.Success(0.milliseconds)
            }
            fired.shouldBeTrue()
         }

         it("should error if invocation count > 1 for containers") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestNameBuilder.builder("foo").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Container,
            )

            InvocationCountCheckInterceptor(TestConfigResolver()).intercept(
               tc.copy(config = TestConfig(invocations = 4)),
               NoopTestScope(tc, coroutineContext)
            ) { _, _ -> TestResult.Success(0.milliseconds) }.isError shouldBe true
         }
      }
   }
}
