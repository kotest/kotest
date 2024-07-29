package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.AbstractTestCaseExecutionListener
import io.kotest.engine.test.scopes.TerminalTestScope
import io.kotest.engine.test.interceptors.TestFinishedInterceptor
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

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
      val context = TerminalTestScope(tc, coroutineContext)
      var finished = false
      val listener = object : AbstractTestCaseExecutionListener() {
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            super.testFinished(testCase, result)
            finished = true
         }
      }

      @Suppress("DEPRECATION") // Remove when removing listeners
      TestFinishedInterceptor(listener).intercept(tc, context) { _, _ -> TestResult.Success(0.seconds) }
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
      val context = TerminalTestScope(tc, coroutineContext)
      var ignored = false
      var r: String? = null
      val listener = object : AbstractTestCaseExecutionListener() {
         override suspend fun testIgnored(testCase: TestCase, reason: String?) {
            ignored = true
            r = reason
         }
      }
      TestFinishedInterceptor(listener).intercept(tc, context) { _, _ -> TestResult.Ignored("wobble") }
      ignored shouldBe true
      r shouldBe "wobble"
   }
})
