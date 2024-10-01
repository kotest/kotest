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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.seconds

private val mutex = Mutex()

/**
 * Invokes any [BeforeSpecListener] callbacks by delegating to [SpecExtensions], if this is the first test that has
 * executed for this instance of the spec. If any callback fails, further tests are skipped and marked as ignored.
 *
 * This spec level callback is executed at the test stage, because we only want to invoke it if
 * there is at least one enabled test. And since tests can be disabled or enabled programatically,
 * we must defer execution until after the test configurations have executed.
 */
internal class BeforeSpecListenerInterceptor(
   private val registry: ExtensionRegistry,
   private val context: EngineContext,
) : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor,
   ): TestResult {
      // check if we need to run beforeSpec and if so do inside the mutex to avoid race conditions
      // the actual test invocation should be outside of the lock
      val runTest: suspend () -> TestResult = mutex.withLock {
         val state = context.state[testCase.spec::class.beforeSpecStateKey()] as? BeforeSpecState
         when {
            state == null -> suspend { test(testCase, scope) }
            state.success.contains(testCase.spec) -> suspend { test(testCase, scope) }
            state.failed.contains(testCase.spec) -> suspend { TestResult.Ignored("Skipped due to beforeSpec failure") }
            else -> SpecExtensions(registry)
               .beforeSpec(testCase.spec)
               .fold(
                  {
                     state.success.add(testCase.spec);
                     suspend { test(testCase, scope) }
                  },
                  {
                     state.failed.add(testCase.spec)
                     state.errors.add(it);
                     { createTestResult(0.seconds, it) }
                  }
               )
         }
      }
      return runTest()
   }
}
