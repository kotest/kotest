package io.kotest.engine.test

import io.kotest.core.Logger
import io.kotest.core.Platform
import io.kotest.core.platform
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.test.interceptors.AssertionModeInterceptor
import io.kotest.engine.test.interceptors.BeforeSpecListenerInterceptor
import io.kotest.engine.test.interceptors.CoroutineDebugProbeInterceptor
import io.kotest.engine.test.interceptors.CoroutineDispatcherFactoryTestInterceptor
import io.kotest.engine.test.interceptors.CoroutineLoggingInterceptor
import io.kotest.engine.test.interceptors.ExpectExceptionTestInterceptor
import io.kotest.engine.test.interceptors.InvocationCountCheckInterceptor
import io.kotest.engine.test.interceptors.InvocationTimeoutInterceptor
import io.kotest.engine.test.interceptors.LifecycleInterceptor
import io.kotest.engine.test.interceptors.NextTestExecutionInterceptor
import io.kotest.engine.test.interceptors.SoftAssertInterceptor
import io.kotest.engine.test.interceptors.SupervisorScopeInterceptor
import io.kotest.engine.test.interceptors.TestCaseExtensionInterceptor
import io.kotest.engine.test.interceptors.TestCoroutineInterceptor
import io.kotest.engine.test.interceptors.TestEnabledCheckInterceptor
import io.kotest.engine.test.interceptors.TestFinishedInterceptor
import io.kotest.engine.test.interceptors.TestNameContextInterceptor
import io.kotest.engine.test.interceptors.DescriptorPathContextInterceptor
import io.kotest.engine.test.interceptors.TimeoutInterceptor
import io.kotest.engine.test.interceptors.blockedThreadTimeoutInterceptor
import io.kotest.engine.test.interceptors.coroutineErrorCollectorInterceptor
import io.kotest.engine.testInterceptorsForPlatform
import kotlin.time.TimeSource

/**
 * Executes a single [TestCase].
 *
 * Uses a [TestCaseExecutionListener] to notify callers of events in the test lifecycle.
 *
 */
internal class TestCaseExecutor(
   private val listener: TestCaseExecutionListener,
   private val context: EngineContext,
) {

   private val logger = Logger(TestCaseExecutor::class)

   suspend fun execute(testCase: TestCase, testScope: TestScope, specContext: SpecContext): TestResult {
      logger.log { Pair(testCase.name.name, "Executing test with scope $testScope") }

      val timeMark = TimeSource.Monotonic.markNow()

      // JS platforms require extra care when runTest is used, so skip it for now.
      // Issue: https://github.com/kotest/kotest/issues/4077
      val useCoroutineTestScope = when (platform) {
         Platform.JVM, Platform.Native -> context.testConfigResolver.coroutineTestScope(testCase)
         Platform.JS, Platform.WasmJs -> if (context.testConfigResolver.coroutineTestScope(testCase)) {
            error("Configuration 'coroutineTestScope' is unsupported on $platform")
         } else false
      }

      val interceptors = listOfNotNull(
         DescriptorPathContextInterceptor,
         TestNameContextInterceptor,
         TestFinishedInterceptor(listener, context.testExtensions()),
         InvocationCountCheckInterceptor(context.testConfigResolver),
         SupervisorScopeInterceptor,
         // the dispatcher factory should run before before/after callbacks so they are executed in the right context
         CoroutineDispatcherFactoryTestInterceptor(context.specConfigResolver),
         if (platform == Platform.JVM) coroutineErrorCollectorInterceptor() else null,
         TestEnabledCheckInterceptor(context.projectConfigResolver, context.specConfigResolver, context.testConfigResolver),
         BeforeSpecListenerInterceptor(context.specExtensions(), specContext),
         TestCaseExtensionInterceptor(context.testExtensions()),
         LifecycleInterceptor(listener, timeMark, context.testExtensions()),
         AssertionModeInterceptor(context.testConfigResolver),
         SoftAssertInterceptor(context.testConfigResolver),
         CoroutineLoggingInterceptor(context.projectConfigResolver, context.testExtensions()),
         if (platform == Platform.JVM)
            blockedThreadTimeoutInterceptor(timeMark, context.testConfigResolver)
         else null,
         TimeoutInterceptor(timeMark, context.testConfigResolver),
         ExpectExceptionTestInterceptor,
         *testInterceptorsForPlatform().toTypedArray(),
         TestInvocationInterceptor(
            timeMark,
            listOfNotNull(
               // Timeout is handled inside TestCoroutineInterceptor if it is enabled
               if (!useCoroutineTestScope) InvocationTimeoutInterceptor(context.testConfigResolver) else null,
               if (useCoroutineTestScope) TestCoroutineInterceptor(context.testConfigResolver) else null,
            ),
            context.testConfigResolver,
            context.testExtensions()
         ),
         CoroutineDebugProbeInterceptor(context.testConfigResolver),
      )

      val innerExecute = NextTestExecutionInterceptor { tc, scope ->
         logger.log { Pair(testCase.name.name, "Executing test") }
         tc.test(scope)
         TestResult.Success(timeMark.elapsedNow())
      }

      return interceptors.foldRight(innerExecute) { ext, fn ->
         NextTestExecutionInterceptor { tc, tscope -> ext.intercept(tc, tscope, fn) }
      }.invoke(testCase, testScope)
   }
}
