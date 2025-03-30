package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.interceptors.TestEnabledCheckInterceptor
import io.kotest.engine.test.scopes.TerminalTestScope
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class EnabledCheckTestExecutionInterceptorTest : FunSpec({

   test("should invoke chain function if test is enabled") {

      val tc = TestCase(
         EnabledCheckTestExecutionInterceptorTest::class.toDescriptor().append("foo"),
         TestNameBuilder.builder("foo").build(),
         EnabledCheckTestExecutionInterceptorTest(),
         {},
         SourceRef.None,
         TestType.Test
      )
      val context = TerminalTestScope(tc, coroutineContext)
      // the test starts with ! so should not be enabled, therefore the chain should be ignored
      var fired = false
      TestEnabledCheckInterceptor(
         ProjectConfigResolver(), SpecConfigResolver(), TestConfigResolver()
      ).intercept(tc, context) { _, _ ->
         fired = true
         TestResult.Success(0.seconds)
      }
      fired shouldBe true
   }

   test("should skip chain function if test is not enabled") {

      val tc = TestCase(
         EnabledCheckTestExecutionInterceptorTest::class.toDescriptor().append("!foo"),
         TestNameBuilder.builder("!foo").build(),
         EnabledCheckTestExecutionInterceptorTest(),
         {},
         SourceRef.None,
         TestType.Test
      )
      val context = TerminalTestScope(tc, coroutineContext)
      // the test starts with ! so should not be enabled, therefore the chain should be ignored
      TestEnabledCheckInterceptor(
         ProjectConfigResolver(), SpecConfigResolver(), TestConfigResolver()
      ).intercept(tc, context) { _, _ -> error("boom") }
   }
})
