package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.contexts.NoopTestContext
import io.kotest.engine.test.interceptors.InvocationCountCheckInterceptor
import io.kotest.matchers.booleans.shouldBeTrue

class InvocationCountCheckInterceptorTest : DescribeSpec() {
   init {
      describe("InvocationCountCheckInterceptor") {

         it("should invoke downstream if invocation count == 1 for containers") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestName("foo"),
               InvocationCountCheckInterceptorTest(),
               {},
               sourceRef(),
               TestType.Container,
            )
            var fired = false
            InvocationCountCheckInterceptor.intercept { _, _ ->
               fired = true
               TestResult.success(0)
            }.invoke(tc.copy(config = tc.config.copy(invocations = 1)), NoopTestContext(tc, coroutineContext))
            fired.shouldBeTrue()
         }

         it("should invoke downstream if invocation count > 1 for tests") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestName("foo"),
               InvocationCountCheckInterceptorTest(),
               {},
               sourceRef(),
               TestType.Test,
            )
            var fired = false
            InvocationCountCheckInterceptor.intercept { _, _ ->
               fired = true
               TestResult.success(0)
            }.invoke(tc.copy(config = tc.config.copy(invocations = 44)), NoopTestContext(tc, coroutineContext))
            fired.shouldBeTrue()
         }

         it("should error if invocation count > 1 for containers") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestName("foo"),
               InvocationCountCheckInterceptorTest(),
               {},
               sourceRef(),
               TestType.Container,
            )
            shouldThrowAny {
               InvocationCountCheckInterceptor.intercept { _, _ -> TestResult.success(0) }
                  .invoke(tc.copy(config = tc.config.copy(invocations = 4)), NoopTestContext(tc, coroutineContext))
            }
         }
      }
   }
}
