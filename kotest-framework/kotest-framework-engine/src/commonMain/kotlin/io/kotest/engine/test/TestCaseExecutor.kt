package io.kotest.engine.test

import io.kotest.core.Logger
import io.kotest.core.Platform
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.platform
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.test.interceptors.AssertionModeInterceptor
import io.kotest.engine.test.interceptors.BeforeSpecListenerInterceptor
import io.kotest.engine.test.interceptors.CoroutineDebugProbeInterceptor
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
import io.kotest.engine.test.interceptors.TestPathContextInterceptor
import io.kotest.engine.test.interceptors.TimeoutInterceptor
import io.kotest.engine.test.interceptors.blockedThreadTimeoutInterceptor
import io.kotest.engine.test.interceptors.coroutineDispatcherFactoryInterceptor
import io.kotest.engine.test.interceptors.coroutineErrorCollectorInterceptor
import io.kotest.engine.testInterceptorsForPlatform
import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * Executes a single [TestCase].
 *
 * Uses a [TestCaseExecutionListener] to notify callers of events in the test lifecycle.
 *
 */
internal class TestCaseExecutor(
   private val listener: TestCaseExecutionListener,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory = NoopCoroutineDispatcherFactory,
   private val context: EngineContext,
) {

   private val logger = Logger(TestCaseExecutor::class)

   suspend fun execute(testCase: TestCase, testScope: TestScope, specContext: SpecContext): TestResult {
      logger.log { Pair(testCase.name.name, "Executing test with scope $testScope") }

      val timeMark = TimeSource.Monotonic.markNow()

      // JS platforms require extra care when runTest is used, so skip it for now.
      // Issue: https://github.com/kotest/kotest/issues/4077
      val useCoroutineTestScope = when (platform) {
         Platform.JVM, Platform.Native -> testCase.config.coroutineTestScope
         Platform.JS, Platform.WasmJs -> if (testCase.config.coroutineTestScope) {
            error("Configuration 'coroutineTestScope' is unsupported on $platform")
         } else false
      }

      val interceptors = listOfNotNull(
         TestPathContextInterceptor,
         TestNameContextInterceptor,
         TestFinishedInterceptor(listener, context.configuration.registry),
         InvocationCountCheckInterceptor,
         SupervisorScopeInterceptor,
         if (platform == Platform.JVM) coroutineDispatcherFactoryInterceptor(defaultCoroutineDispatcherFactory) else null,
         if (platform == Platform.JVM) coroutineErrorCollectorInterceptor() else null,
         TestEnabledCheckInterceptor(context.configuration),
         BeforeSpecListenerInterceptor(context.configuration.registry, specContext),
         TestCaseExtensionInterceptor(context.configuration.registry),
         LifecycleInterceptor(listener, timeMark, context.configuration.registry),
         AssertionModeInterceptor,
         SoftAssertInterceptor(),
         CoroutineLoggingInterceptor(context.configuration),
         if (platform == Platform.JVM) blockedThreadTimeoutInterceptor(context.configuration, timeMark) else null,
         TimeoutInterceptor(timeMark),
         ExpectExceptionTestInterceptor,
         *testInterceptorsForPlatform().toTypedArray(),
         TestInvocationInterceptor(
            context.configuration.registry,
            timeMark,
            listOfNotNull(
               // Timeout is handled inside TestCoroutineInterceptor if it is enabled
               if (!useCoroutineTestScope) InvocationTimeoutInterceptor else null,
               if (useCoroutineTestScope) TestCoroutineInterceptor() else null,
            )
         ),
         CoroutineDebugProbeInterceptor,
      )

      val innerExecute = NextTestExecutionInterceptor { tc, scope ->
         logger.log { Pair(testCase.name.name, "Executing test") }
         tc.test(scope)
         try {
            TestResult.Success(timeMark.elapsedNow())
         } catch (e: Throwable) {
            TestResult.Success(Duration.ZERO) // workaround for kotlin 1.5
         }
      }

      return interceptors.foldRight(innerExecute) { ext, fn ->
         NextTestExecutionInterceptor { tc, tscope -> ext.intercept(tc, tscope, fn) }
      }.invoke(testCase, testScope)
   }
}
