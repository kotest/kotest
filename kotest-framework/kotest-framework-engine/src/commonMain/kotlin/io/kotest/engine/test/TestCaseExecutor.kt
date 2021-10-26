package io.kotest.engine.test

import io.kotest.common.ExperimentalKotest
import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.test.interceptors.AssertionModeInterceptor
import io.kotest.engine.test.interceptors.CoroutineDebugProbeInterceptor
import io.kotest.engine.test.interceptors.CoroutineLoggingInterceptor
import io.kotest.engine.test.interceptors.CoroutineScopeInterceptor
import io.kotest.engine.test.interceptors.EnabledCheckInterceptor
import io.kotest.engine.test.interceptors.ExceptionCapturingInterceptor
import io.kotest.engine.test.interceptors.SoftAssertInterceptor
import io.kotest.engine.test.interceptors.InvocationCountCheckInterceptor
import io.kotest.engine.test.interceptors.InvocationRepeatInterceptor
import io.kotest.engine.test.interceptors.InvocationTimeoutInterceptor
import io.kotest.engine.test.interceptors.LifecycleInterceptor
import io.kotest.engine.test.interceptors.SupervisorScopeInterceptor
import io.kotest.engine.test.interceptors.TestCaseExtensionInterceptor
import io.kotest.engine.test.interceptors.TestCoroutineDispatcherInterceptor
import io.kotest.engine.test.interceptors.TestFinishedInterceptor
import io.kotest.engine.test.interceptors.TimeoutInterceptor
import io.kotest.engine.test.interceptors.blockedThreadTimeoutInterceptor
import io.kotest.engine.test.interceptors.coroutineDispatcherFactoryInterceptor
import io.kotest.engine.test.interceptors.coroutineErrorCollectorInterceptor
import io.kotest.engine.test.interceptors.isTestCoroutineDispatcher
import io.kotest.mpp.log
import kotlin.time.TimeSource

/**
 * Executes a single [TestCase].
 *
 * Uses a [TestCaseExecutionListener] to notify callers of events in the test lifecycle.
 *
 */
@OptIn(ExperimentalKotest::class)
class TestCaseExecutor(
   private val listener: TestCaseExecutionListener,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory = NoopCoroutineDispatcherFactory,
   private val configuration: Configuration,
) {

   suspend fun execute(testCase: TestCase, context: TestContext): TestResult {
      log { "TestCaseExecutor: execute entry point '${testCase.descriptor.path().value}' context=$context" }

      val timeMark = TimeSource.Monotonic.markNow()

      val interceptors = listOfNotNull(
         InvocationCountCheckInterceptor,
         CoroutineDebugProbeInterceptor(configuration),
         SupervisorScopeInterceptor,
         if (platform == Platform.JVM) coroutineDispatcherFactoryInterceptor(defaultCoroutineDispatcherFactory) else null,
         if (platform == Platform.JVM) coroutineErrorCollectorInterceptor() else null,
         TestFinishedInterceptor(listener),
         TestCaseExtensionInterceptor(configuration.registry()),
         EnabledCheckInterceptor(configuration),
         LifecycleInterceptor(listener, timeMark, configuration.registry()),
         ExceptionCapturingInterceptor(timeMark),
         AssertionModeInterceptor(),
         SoftAssertInterceptor(),
         CoroutineScopeInterceptor,
         if (platform == Platform.JVM) blockedThreadTimeoutInterceptor(configuration) else null,
         TimeoutInterceptor(configuration),
         InvocationRepeatInterceptor(configuration.registry(), timeMark),
         InvocationTimeoutInterceptor(configuration),
         CoroutineLoggingInterceptor(configuration),
         if (platform == Platform.JVM && testCase.isTestCoroutineDispatcher(configuration)) TestCoroutineDispatcherInterceptor() else null,
      )

      val innerExecute: suspend (TestCase, TestContext) -> TestResult = { tc, ctx ->
         tc.test(ctx)
         createTestResult(timeMark.elapsedNow(), null)
      }

      val result = interceptors.foldRight(innerExecute) { ext, fn ->
         { tc, ctx -> ext.intercept(fn)(tc, ctx) }
      }.invoke(testCase, context)

      return result
   }
}
