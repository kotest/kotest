package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.descriptors.append
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType
import io.kotest.engine.test.contexts.TerminalTestContext
import io.kotest.engine.test.interceptors.ExceptionCapturingInterceptor
import io.kotest.matchers.shouldBe

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
      val context = TerminalTestContext(tc, coroutineContext)

      ExceptionCapturingInterceptor(5).intercept { _, _ -> throw AssertionError("boom") }
         .invoke(tc, context)
         .status shouldBe TestStatus.Failure

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
      val context = TerminalTestContext(tc, coroutineContext)

      ExceptionCapturingInterceptor(5).intercept { _, _ -> error("boom") }
         .invoke(tc, context)
         .status shouldBe TestStatus.Error

   }
})
