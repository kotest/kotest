package io.kotest.engine.test.interceptors

import io.kotest.engine.config.ExtensionRegistry
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.test.createTestResult
import kotlin.time.Duration.Companion.seconds

/**
 * Invokes any [BeforeSpecListener] callbacks by delegating to [SpecExtensions], if this is the first test that has
 * executed for this instance of the spec. If any callback fails, further tests are skipped and marked as ignored.
 *
 * This spec level callback is executed at the test stage, because we only want to invoke it if
 * there is at least one enabled test. And since tests can be disabled or enabled programatically,
 * we must defer execution until after the test blocks have been registered (if any).
 */
internal class BeforeSpecListenerInterceptor(
  private val registry: ExtensionRegistry,
  private val specContext: SpecContext,
) : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor,
   ): TestResult {

      val shouldRun = specContext.beforeSpecInvoked.compareAndSet(
         expect = false,
         update = true
      )

      return if (shouldRun) {
         SpecExtensions(registry)
            .beforeSpec(testCase.spec)
            .fold(
               {
                  test(testCase, scope)
               },
               {
                  specContext.beforeSpecError = it
                  createTestResult(0.seconds, it)
               }
            )
      } else {
         if (specContext.beforeSpecError == null)
            test(testCase, scope)
         else
            TestResult.Ignored("Skipped due to beforeSpec failure")
      }
   }
}
