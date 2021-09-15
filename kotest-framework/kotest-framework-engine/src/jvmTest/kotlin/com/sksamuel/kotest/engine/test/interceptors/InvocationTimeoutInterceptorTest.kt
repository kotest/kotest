package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.descriptors.append
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.contexts.NoopTestContext
import io.kotest.engine.test.interceptors.InvocationTimeoutInterceptor
import io.kotest.engine.test.interceptors.TestTimeoutException
import kotlinx.coroutines.delay
import kotlin.time.Duration

class InvocationTimeoutInterceptorTest : FunSpec() {
   init {
      test("InvocationTimeoutInterceptor should throw TestTimeoutException after timeout") {

         val tc = TestCase(
            InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
            TestName("foo"),
            InvocationCountCheckInterceptorTest(),
            {},
            sourceRef(),
            TestType.Test,
         )

         shouldThrow<TestTimeoutException> {
            InvocationTimeoutInterceptor.intercept { _, _ ->
               delay(10000)
               TestResult.success(0)
            }.invoke(
               tc.copy(config = tc.config.copy(invocationTimeout = Duration.milliseconds(1))),
               NoopTestContext(tc, coroutineContext)
            )
         }
      }
   }
}
