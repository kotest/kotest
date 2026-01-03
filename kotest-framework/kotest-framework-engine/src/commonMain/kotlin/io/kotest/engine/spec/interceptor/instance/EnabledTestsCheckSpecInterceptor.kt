package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.flatMap
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.enabled.TestEnabledChecker

/**
 * A [SpecInterceptor] that skips a spec that has no enabled tests at runtime.
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
      return if (tests.isEmpty()) {
         val reason = "Ignored due to no defined tests"
         runCatching { context.listener.specIgnored(spec::class, reason) }
            .flatMap { context.specExtensions().ignored(spec::class, reason) }
            .map { emptyMap() }
      } else {
         val hasEnabledTests = tests.any { checker.isEnabled(it).isEnabled }
         if (hasEnabledTests) {
            next.invoke(spec)
         } else {
            val reason = "Ignored due to no enabled tests"
            runCatching { context.listener.specIgnored(spec::class, reason) }
               .flatMap { context.specExtensions().ignored(spec::class, reason) }
               .map { emptyMap() }
         }
      }
   }
}
