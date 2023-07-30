@file:OptIn(ExperimentalStdlibApi::class)

package io.kotest.engine.test

import io.kotest.common.ExperimentalKotest
import io.kotest.common.MonotonicTimeSourceCompat
import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.test.interceptors.AssertionModeInterceptor
import io.kotest.engine.test.interceptors.CoroutineDebugProbeInterceptor
import io.kotest.engine.test.interceptors.CoroutineLoggingInterceptor
import io.kotest.engine.test.interceptors.EnabledCheckInterceptor
import io.kotest.engine.test.interceptors.InvocationCountCheckInterceptor
import io.kotest.engine.test.interceptors.InvocationTimeoutInterceptor
import io.kotest.engine.test.interceptors.LifecycleInterceptor
import io.kotest.engine.test.interceptors.SoftAssertInterceptor
import io.kotest.engine.test.interceptors.SupervisorScopeInterceptor
import io.kotest.engine.test.interceptors.TestCaseExtensionInterceptor
import io.kotest.engine.test.interceptors.TestCoroutineInterceptor
import io.kotest.engine.test.interceptors.TestDispatcherInterceptor
import io.kotest.engine.test.interceptors.TestFinishedInterceptor
import io.kotest.engine.test.interceptors.TestNameContextInterceptor
import io.kotest.engine.test.interceptors.TestPathContextInterceptor
import io.kotest.engine.test.interceptors.TimeoutInterceptor
import io.kotest.engine.test.interceptors.assertionModeThreadLocalContextInterceptor
import io.kotest.engine.test.interceptors.blockedThreadTimeoutInterceptor
import io.kotest.engine.test.interceptors.coroutineDispatcherFactoryInterceptor
import io.kotest.engine.test.interceptors.coroutineErrorCollectorInterceptor
import io.kotest.engine.testInterceptorsForPlatform
import io.kotest.mpp.Logger
import kotlin.time.Duration

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
   private val configuration: ProjectConfiguration,
) {

   private val logger = Logger(TestCaseExecutor::class)

   @OptIn(ExperimentalStdlibApi::class)
   suspend fun execute(testCase: TestCase, testScope: TestScope): TestResult {
      logger.log { Pair(testCase.name.testName, "Executing test with scope $testScope") }

      val timeMark = MonotonicTimeSourceCompat.markNow()

      val interceptors = listOfNotNull(
         TestPathContextInterceptor,
         TestNameContextInterceptor,
         TestFinishedInterceptor(listener),
         InvocationCountCheckInterceptor,
         SupervisorScopeInterceptor,
         if (platform == Platform.JVM) coroutineDispatcherFactoryInterceptor(defaultCoroutineDispatcherFactory) else null,
         if (platform == Platform.JVM) coroutineErrorCollectorInterceptor() else null,
         TestCaseExtensionInterceptor(configuration.registry),
         EnabledCheckInterceptor(configuration),
         LifecycleInterceptor(listener, timeMark, configuration.registry),
         if (platform == Platform.JVM) assertionModeThreadLocalContextInterceptor() else null,
         AssertionModeInterceptor,
         SoftAssertInterceptor(),
         CoroutineLoggingInterceptor(configuration),
         if (platform == Platform.JVM) blockedThreadTimeoutInterceptor(configuration, timeMark) else null,
         TimeoutInterceptor(timeMark),
         *testInterceptorsForPlatform().toTypedArray(),
         TestInvocationInterceptor(
            configuration.registry,
            timeMark,
            listOfNotNull(
               InvocationTimeoutInterceptor,
               if (platform == Platform.JVM && testCase.config.testCoroutineDispatcher) TestDispatcherInterceptor() else null,
               if (platform != Platform.JS && testCase.config.coroutineTestScope) TestCoroutineInterceptor() else null,
            )
         ),
         CoroutineDebugProbeInterceptor,
      )

      val innerExecute: suspend (TestCase, TestScope) -> TestResult = { tc, scope ->
         logger.log { Pair(testCase.name.testName, "Executing test") }
         tc.test(scope)
         try {
            TestResult.Success(timeMark.elapsedNow())
         } catch (e: Throwable) {
            TestResult.Success(Duration.ZERO) // workaround for kotlin 1.5
         }
      }

      return interceptors.foldRight(innerExecute) { ext, fn ->
         { tc, sc -> ext.intercept(tc, sc, fn) }
      }.invoke(testCase, testScope)
   }
}
