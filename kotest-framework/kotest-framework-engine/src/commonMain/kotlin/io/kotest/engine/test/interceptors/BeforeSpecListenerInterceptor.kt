package io.kotest.engine.test.interceptors

import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestResultBuilder
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Invokes any [BeforeSpecListener] callbacks by delegating to [SpecExtensions], if this is the first test that has
 * executed for this instance of the spec. If any callback fails, further tests are skipped and marked as ignored.
 *
 * This spec level callback is executed at the test stage, because we only want to invoke it if
 * there is at least one enabled test. And since tests can be disabled or enabled programatically,
 * we must defer execution until after the test blocks have been registered (if any).
 */
@OptIn(ExperimentalAtomicApi::class)
internal class BeforeSpecListenerInterceptor(
   private val specExtensions: SpecExtensions,
   private val specContext: SpecContext,
) : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor,
   ): TestResult {
      
      val shouldRun = specContext.beforeSpecInvoked.compareAndSet(
         expectedValue = false,
         newValue = true,
      )
      
      return if (shouldRun) {
         specExtensions
            .beforeSpec(testCase.spec)
            .fold(
               {
                  specContext.beforeSpecCompletion.complete(Unit)
                  test(testCase, scope)
               },
               {
                  specContext.beforeSpecError = it
                  specContext.beforeSpecCompletion.completeExceptionally(it)
                  TestResultBuilder.builder().withError(it).build()
               }
            )
      } else {
         try {
            specContext.beforeSpecCompletion.await()
            if (specContext.beforeSpecError == null) {
               test(testCase, scope)
            } else {
               TestResultBuilder.builder().withIgnoreReason("Skipped due to beforeSpec failure").build()
            }
         } catch (e: Exception) {
            TestResultBuilder.builder().withIgnoreReason("Skipped due to beforeSpec failure").build()
         }
      }
   }
}