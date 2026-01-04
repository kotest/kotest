package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.enabled.TestEnabledChecker

/**
 * A [SpecInterceptor] that checks a spec to see if it has any enabled tests.
 * If it does not, then further processing is skipped and any ignored tests are sent to the listener.
 */
internal class EnabledTestsCheckSpecInterceptor(
   private val context: EngineContext,
) : SpecInterceptor {

   private val materializer = Materializer(context.specConfigResolver)
   private val checker = TestEnabledChecker(
      context.projectConfigResolver,
      context.specConfigResolver,
      context.testConfigResolver
   )

   override suspend fun intercept(
      spec: Spec,
      next: NextSpecInterceptor
   ): Result<Map<TestCase, TestResult>> {
      val tests = materializer.materialize(spec)
      val hasEnabledTests = tests.any { checker.isEnabled(it).isEnabled }
      return if (hasEnabledTests) {
         next.invoke(spec)
      } else {
         // we will mark all tests as ignored, added to the spec, so they appear in output
         // we consider tests ignored at the test level to just ignore the test not the entire spec,
         // even if the spec ends up having nothing but ignored tests
         runCatching {
            tests.forEach {
               context.listener.testIgnored(it, null)
            }
         }.map { emptyMap() }
      }
   }
}
