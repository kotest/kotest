package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.contexts.TerminalTestContext
import io.kotest.engine.test.interceptors.TestFinishedInterceptor
import io.kotest.matchers.shouldBe

class TestFinishedExecutionInterceptorTest : FunSpec({

   test("should notify of test finishes") {
      val tc = TestCase(
         TestFinishedExecutionInterceptorTest::class.toDescriptor().append("foo"),
         TestName("foo"),
         TestFinishedExecutionInterceptorTest(),
         {},
         sourceRef(),
         TestType.Test
      )
      val context = TerminalTestContext(tc, coroutineContext)
      var finished = false
      val listener = object : TestCaseExecutionListener {
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            super.testFinished(testCase, result)
            finished = true
         }
      }
      TestFinishedInterceptor(listener).intercept { _, _ -> TestResult.success(0) }.invoke(tc, context)
      finished shouldBe true
   }

   test("should notify of test ignores") {
      val tc = TestCase(
         TestFinishedExecutionInterceptorTest::class.toDescriptor().append("!foo"),
         TestName("!foo"),
         TestFinishedExecutionInterceptorTest(),
         {},
         sourceRef(),
         TestType.Test
      )
      val context = TerminalTestContext(tc, coroutineContext)
      var ignored = false
      val listener = object : TestCaseExecutionListener {
         override suspend fun testIgnored(testCase: TestCase) {
            ignored = true
         }
      }
      TestFinishedInterceptor(listener).intercept { _, _ -> TestResult.ignored(null) }.invoke(tc, context)
      ignored shouldBe true
   }
})
