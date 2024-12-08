package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.flatMap
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor

/**
 * Invokes any [AfterSpecListener] callbacks for the given spec.
 *
 * These listeners are only invoked if the spec has at least one defined test AND all defined tests are not ignored,
 * which is to say that the same logic as before spec is used. And no after spec listeners are invoked if there
 * are before spec listeners that have not been invoked.
 */
internal class AfterSpecListenerInterceptor(
   private val registry: ExtensionRegistry,
) : SpecInterceptor {
   override suspend fun intercept(
      spec: Spec,
      next: NextSpecInterceptor,
   ): Result<Map<TestCase, TestResult>> {
      return next.invoke(spec).flatMap { results ->
         if (hasActiveTest(results)) {
            SpecExtensions(registry)
               .afterSpec(spec)
               .map { results }
         } else {
            Result.success(results)
         }
      }
   }

   // the spec results are considered active if at least test has a non ignored status
   private fun hasActiveTest(results: Map<TestCase, TestResult>): Boolean {
      return results.any { it.value !is TestResult.Ignored }
   }
}
