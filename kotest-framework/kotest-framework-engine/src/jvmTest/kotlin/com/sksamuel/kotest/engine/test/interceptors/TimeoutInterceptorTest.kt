package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.config.Configuration
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.contexts.NoopTestContext
import io.kotest.engine.test.interceptors.TestTimeoutException
import io.kotest.engine.test.interceptors.TimeoutInterceptor
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.milliseconds

class TimeoutInterceptorTest : FunSpec() {
   init {
      test("TimeoutInterceptor should throw TestTimeoutException after timeout") {

         val tc = TestCase(
            InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
            TestName("foo"),
            InvocationCountCheckInterceptorTest(),
            {},
            sourceRef(),
            TestType.Test,
         )

         shouldThrow<TestTimeoutException> {
            TimeoutInterceptor(Configuration()).intercept { _, _ ->
               delay(10000)
               TestResult.Success(0.milliseconds)
            }.invoke(
               tc.copy(config = tc.config.copy(timeout = Duration.milliseconds(1))),
               NoopTestContext(tc, coroutineContext)
            )
         }
      }
   }
}
