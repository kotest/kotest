package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.NoopTestContext
import io.kotest.engine.test.interceptors.InvocationCountCheckInterceptor

class InvocationCountCheckInterceptorTest : DescribeSpec() {
   init {
      describe("InvocationCountCheckInterceptor") {
         it("should error if invocation count > 1 for containers") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescription().appendTest("foo"),
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
