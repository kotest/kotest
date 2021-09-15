package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.descriptors.append
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.contexts.TerminalTestContext
import io.kotest.engine.test.interceptors.EnabledCheckInterceptor
import io.kotest.matchers.shouldBe

class EnabledCheckTestExecutionInterceptorTest : FunSpec({

   test("should invoke chain function if test is enabled") {

      val tc = TestCase(
         EnabledCheckTestExecutionInterceptorTest::class.toDescriptor().append("foo"),
         TestName("foo"),
         EnabledCheckTestExecutionInterceptorTest(),
         {},
         sourceRef(),
         TestType.Test
      )
      val context = TerminalTestContext(tc, coroutineContext)
      // the test starts with ! so should not be enabled, therefore the chain should be ignored
      var fired = false
      EnabledCheckInterceptor.intercept { _, _ ->
         fired = true
         TestResult.success(0)
      }.invoke(tc, context)
      fired shouldBe true
   }

   test("should skip chain function if test is not enabled") {

      val tc = TestCase(
         EnabledCheckTestExecutionInterceptorTest::class.toDescriptor().append("!foo"),
         TestName("!foo"),
         EnabledCheckTestExecutionInterceptorTest(),
         {},
         sourceRef(),
         TestType.Test
      )
      val context = TerminalTestContext(tc, coroutineContext)
      // the test starts with ! so should not be enabled, therefore the chain should be ignored
      EnabledCheckInterceptor.intercept { _, _ -> error("boom") }.invoke(tc, context)
   }

})
