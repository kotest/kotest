package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.config.Configuration
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.scopes.TerminalTestScope
import io.kotest.engine.test.interceptors.EnabledCheckInterceptor
import io.kotest.matchers.shouldBe
import kotlin.time.seconds

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
      val context = TerminalTestScope(tc, coroutineContext)
      // the test starts with ! so should not be enabled, therefore the chain should be ignored
      var fired = false
      EnabledCheckInterceptor(Configuration()).intercept(tc, context) { _, _ ->
         fired = true
         TestResult.Success(0.seconds)
      }
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
      val context = TerminalTestScope(tc, coroutineContext)
      // the test starts with ! so should not be enabled, therefore the chain should be ignored
      EnabledCheckInterceptor(Configuration()).intercept(tc, context) { _, _ -> error("boom") }
   }

})
