package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.interceptors.InvocationTimeoutInterceptor
import io.kotest.engine.test.scopes.NoopTestScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.milliseconds

class InvocationTimeoutInterceptorTest : FunSpec() {
   init {
      test("InvocationTimeoutInterceptor should error after timeout") {

         val tc = TestCase(
            InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
            TestName("foo"),
            InvocationCountCheckInterceptorTest(),
            {},
            sourceRef(),
            TestType.Test,
         )

         InvocationTimeoutInterceptor(TimeSource.Monotonic.markNow()).intercept(
            tc.copy(config = tc.config.copy(invocationTimeout = Duration.milliseconds(1))),
            NoopTestScope(tc, coroutineContext)
         ) { _, _ ->
            delay(10000)
            TestResult.Success(0.milliseconds)
         }.isError shouldBe true
      }
   }
}
