package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.delayController
import io.kotest.engine.test.DefaultTestScope
import io.kotest.engine.test.interceptors.TestCoroutineDispatcherInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.milliseconds

@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
class RunBlockingTestInterceptorTest : FunSpec() {
   init {
      test("RunBlockingTestInterceptor should install a DelayController") {

         val tc = TestCase(
            InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
            TestName("foo"),
            InvocationCountCheckInterceptorTest(),
            {},
            sourceRef(),
            TestType.Test,
         )

         var fired = false
         TestCoroutineDispatcherInterceptor().intercept(tc, DefaultTestScope(tc, coroutineContext)) { _, context ->
            context.delayController.shouldNotBeNull()
            fired = true
            TestResult.Success(0.milliseconds)
         }
         fired.shouldBeTrue()

      }
   }
}
