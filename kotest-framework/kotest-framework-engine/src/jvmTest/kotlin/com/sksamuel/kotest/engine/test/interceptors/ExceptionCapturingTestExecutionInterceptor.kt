package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.common.MonotonicTimeSourceCompat
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.test.interceptors.ExceptionCapturingInterceptor
import io.kotest.engine.test.scopes.TerminalTestScope
import io.kotest.matchers.booleans.shouldBeTrue

class ExceptionCapturingTestExecutionInterceptorTest : FunSpec({

   test("ExceptionCapturingTestExecutionInterceptor should capture assertion errors") {

      val tc = TestCase(
         ExceptionCapturingTestExecutionInterceptorTest::class.toDescriptor().append("foo"),
         TestName("foo"),
         ExceptionCapturingTestExecutionInterceptorTest(),
         {},
         sourceRef(),
         TestType.Test
      )
      val context = TerminalTestScope(tc, coroutineContext)

      ExceptionCapturingInterceptor(MonotonicTimeSourceCompat.markNow())
         .intercept(tc, context) { _, _ -> throw AssertionError("boom") }
         .isFailure.shouldBeTrue()

   }

   test("ExceptionCapturingTestExecutionInterceptor should capture exceptions") {

      val tc = TestCase(
         ExceptionCapturingTestExecutionInterceptorTest::class.toDescriptor().append("foo"),
         TestName("foo"),
         ExceptionCapturingTestExecutionInterceptorTest(),
         {},
         sourceRef(),
         TestType.Test
      )
      val context = TerminalTestScope(tc, coroutineContext)

      ExceptionCapturingInterceptor(MonotonicTimeSourceCompat.markNow())
         .intercept(tc, context) { _, _ -> error("boom") }
         .isError.shouldBeTrue()

   }
})
