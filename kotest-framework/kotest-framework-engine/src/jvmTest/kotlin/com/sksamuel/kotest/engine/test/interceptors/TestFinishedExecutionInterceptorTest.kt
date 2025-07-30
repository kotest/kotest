package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.IgnoredTestListener
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.config.TestConfigResolver
import io.kotest.core.descriptors.toDescriptor
import io.kotest.engine.test.AbstractTestCaseExecutionListener
import io.kotest.engine.test.TestExtensions
import io.kotest.engine.test.interceptors.TestFinishedInterceptor
import io.kotest.engine.test.scopes.TerminalTestScope
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class TestFinishedExecutionInterceptorTest : FunSpec({

   test("should notify of test finishes") {
      val tc = TestCase(
         TestFinishedExecutionInterceptorTest::class.toDescriptor().append("foo"),
         TestNameBuilder.builder("foo").build(),
         TestFinishedExecutionInterceptorTest(),
         {},
         SourceRef.None,
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
      TestFinishedInterceptor(listener, mockk()).intercept(tc, context) { _, _ -> TestResult.Success(0.seconds) }
      finished shouldBe true
   }

   test("should notify of test ignores") {
      val tc = TestCase(
         TestFinishedExecutionInterceptorTest::class.toDescriptor().append("!foo"),
         TestNameBuilder.builder("!foo").build(),
         TestFinishedExecutionInterceptorTest(),
         {},
         SourceRef.None,
         TestType.Test
      )
      val context = TerminalTestScope(tc, coroutineContext)
      val testCaseExecutionListener = object : AbstractTestCaseExecutionListener() {
         var ignored = false
         var reason: String? = null

         override suspend fun testIgnored(testCase: TestCase, reason: String?) {
            this.ignored = true
            this.reason = reason
         }
      }
      val ignoredTestListener = object : IgnoredTestListener {
         var ignored = false
         var reason: String? = null

         override suspend fun ignoredTest(testCase: TestCase, reason: String?) {
            this.ignored = true
            this.reason = reason
         }
      }

      val c = object : AbstractProjectConfig() {
         override val extensions: List<Extension> = listOf(ignoredTestListener)
      }

      TestFinishedInterceptor(testCaseExecutionListener, TestExtensions(TestConfigResolver(c))).intercept(
         tc,
         context
      ) { _, _ -> TestResult.Ignored("wobble") }
      testCaseExecutionListener.ignored shouldBe true
      testCaseExecutionListener.reason shouldBe "wobble"
      ignoredTestListener.ignored shouldBe true
      ignoredTestListener.reason shouldBe "wobble"
   }
})
