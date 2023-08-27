package io.kotest.engine.test.interceptors

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.ref.BeforeSpecState
import io.kotest.engine.spec.interceptor.ref.beforeSpecStateKey
import io.kotest.engine.test.createTestResult
import kotlin.time.Duration.Companion.seconds

/**
 * Invokes any [BeforeSpecListener] callbacks by delegating to [SpecExtensions], if this is the first test that has
 * executed for this instance of the spec. If any callback fails, further tests are skipped and marked as ignored.
 *
 * This spec level callback is executed at the test stage, because we only want to invoke it if
 * there is at least one enabled test. And since tests can be disabled or enabled programatically,
 * we must defer execution until after the test configurations have executed.
 */
internal class BeforeSpecCallbackInterceptor(
   private val registry: ExtensionRegistry,
   private val context: EngineContext,
) : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      val state = context.state[testCase.spec::class.beforeSpecStateKey()] as? BeforeSpecState
      return when {
         state == null -> test(testCase, scope) // skip when not defined, ie in tests
         state.success.contains(testCase.spec) -> test(testCase, scope)
         state.failed.contains(testCase.spec) -> TestResult.Ignored("Skipped due to beforeSpec failure")
         else -> SpecExtensions(registry)
            .beforeSpec(testCase.spec)
            .fold(
               {
                  state.success.add(testCase.spec)
                  test.invoke(testCase, scope)
               },
               {
                  state.failed.add(testCase.spec)
                  state.errors.add(it)
                  createTestResult(0.seconds, it)
               }
            )
      }
   }
}
