package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.TerminalTestContext
import io.kotest.engine.test.interceptors.EnabledCheckInterceptor
import io.kotest.matchers.shouldBe

class EnabledCheckTestExecutionInterceptorTest : FunSpec({

   test("should invoke chain function if test is enabled") {

      val tc = TestCase(
         EnabledCheckTestExecutionInterceptorTest::class.toDescription().appendTest("foo"),
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
         EnabledCheckTestExecutionInterceptorTest::class.toDescription().appendTest("!foo"),
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
