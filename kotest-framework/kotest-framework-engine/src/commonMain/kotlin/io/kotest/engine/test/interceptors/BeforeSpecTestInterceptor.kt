package io.kotest.engine.test.interceptors

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.test.createTestResult
import io.kotest.mpp.bestName
import kotlin.time.Duration.Companion.seconds

/**
 * Invokes any beforeSpec callbacks if this is the first test that has executed for a given spec.
 * If the callback fails, further tests are not invoked.
 */
class BeforeSpecTestInterceptor(
   private val registry: ExtensionRegistry,
   private val context: EngineContext,
) : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {

      return when {
         context.state.containsKey(specSuccessKey(testCase.spec)) -> test(testCase, scope)
         context.state.containsKey(specErrorKey(testCase.spec)) -> TestResult.Ignored("Skipped due to beforeSpec failure")
         else -> SpecExtensions(registry)
            .beforeSpec(testCase.spec)
            .fold(
               {
                  context.state[specSuccessKey(testCase.spec)] = true
                  test.invoke(testCase, scope)
               },
               {
                  context.state[specErrorKey(testCase.spec)] = it
                  createTestResult(0.seconds, it)
               }
            )
      }
   }

   private fun specSuccessKey(spec: Spec) = "before_spec_success_" + spec::class.bestName() + spec.hashCode()
   private fun specErrorKey(spec: Spec) = "before_spec_success_" + spec::class.bestName() + spec.hashCode()
}
