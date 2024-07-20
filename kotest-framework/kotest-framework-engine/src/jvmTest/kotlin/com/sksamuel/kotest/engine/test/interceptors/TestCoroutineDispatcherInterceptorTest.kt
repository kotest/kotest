package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.engine.test.interceptors.TestDispatcherInterceptor
import io.kotest.engine.test.scopes.NoopTestScope
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class TestCoroutineDispatcherInterceptorTest : FunSpec() {
   init {
      test("TestCoroutineDispatcherInterceptor should install a DelayController") {

         val tc = TestCase(
            InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
            TestName("foo"),
            InvocationCountCheckInterceptorTest(),
            {},
            sourceRef(),
            TestType.Test,
         )

         var fired = false
         TestDispatcherInterceptor().intercept(tc, NoopTestScope(tc, coroutineContext)) { _, testScope ->
            testScope.testCoroutineScheduler.shouldNotBeNull()
            fired = true
            TestResult.Success(0.milliseconds)
         }
         fired.shouldBeTrue()

      }
   }
}
