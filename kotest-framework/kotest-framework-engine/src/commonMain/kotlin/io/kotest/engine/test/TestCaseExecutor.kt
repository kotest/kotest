package io.kotest.engine.test

import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.core.concurrency.CoroutineDispatcherFactory
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
import io.kotest.engine.test.interceptors.GlobalSoftAssertInterceptor
import io.kotest.engine.test.interceptors.InvocationCountCheckInterceptor
import io.kotest.engine.test.interceptors.InvocationRepeatInterceptor
import io.kotest.engine.test.interceptors.InvocationTimeoutInterceptor
import io.kotest.engine.test.interceptors.LifecycleInterceptor
import io.kotest.engine.test.interceptors.SupervisorScopeInterceptor
import io.kotest.engine.test.interceptors.TestCaseExtensionInterceptor
import io.kotest.engine.test.interceptors.TestFinishedInterceptor
import io.kotest.engine.test.interceptors.TimeoutInterceptor
import io.kotest.engine.test.interceptors.blockedThreadTimeoutInterceptor
import io.kotest.engine.test.interceptors.coroutineDispatcherFactoryInterceptor
import io.kotest.engine.test.interceptors.coroutineErrorCollectorInterceptor
import io.kotest.mpp.log
import io.kotest.mpp.timeInMillis

/**
 * Executes a single [TestCase].
 *
 * Uses a [TestCaseExecutionListener] to notify callers of events in the test lifecycle.
 *
 */
class TestCaseExecutor(
   private val listener: TestCaseExecutionListener,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory = NoopCoroutineDispatcherFactory,
) {

   suspend fun execute(testCase: TestCase, context: TestContext): TestResult {
      log { "TestCaseExecutor: execute entry point '${testCase.descriptor.path().value}' context=$context" }

      val start = timeInMillis()

      val interceptors = listOfNotNull(
         InvocationCountCheckInterceptor,
         CoroutineDebugProbeInterceptor,
         SupervisorScopeInterceptor,
         if (platform == Platform.JVM) coroutineDispatcherFactoryInterceptor(defaultCoroutineDispatcherFactory) else null,
         if (platform == Platform.JVM) coroutineErrorCollectorInterceptor() else null,
         TestFinishedInterceptor(listener),
         TestCaseExtensionInterceptor,
         EnabledCheckInterceptor,
         LifecycleInterceptor(listener, start),
         ExceptionCapturingInterceptor(start),
         AssertionModeInterceptor,
         GlobalSoftAssertInterceptor,
         CoroutineScopeInterceptor,
         if (platform == Platform.JVM) blockedThreadTimeoutInterceptor() else null,
         TimeoutInterceptor,
         InvocationRepeatInterceptor(start),
         InvocationTimeoutInterceptor,
         CoroutineLoggingInterceptor,
      )

      val innerExecute: suspend (TestCase, TestContext) -> TestResult = { tc, ctx ->
         tc.test(ctx)
         createTestResult(timeInMillis() - start, null)
      }

      val result = interceptors.foldRight(innerExecute) { ext, fn ->
         { tc, ctx -> ext.intercept(fn)(tc, ctx) }
      }.invoke(testCase, context)

      return result
   }
}
