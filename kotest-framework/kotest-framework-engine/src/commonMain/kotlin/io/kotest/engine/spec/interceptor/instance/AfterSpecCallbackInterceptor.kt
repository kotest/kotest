package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.flatMap
import io.kotest.engine.spec.SpecExtensionsExecutor
import io.kotest.engine.spec.interceptor.SpecInterceptor

/**
 * Invokes any [AfterSpecListener] callbacks for the given spec.
 * These listeners are only invoked if the spec has at least one defined test AND all defined tests are not ignored.
 */
internal class AfterSpecCallbackInterceptor(private val registry: ExtensionRegistry) : SpecInterceptor {
   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return fn(spec).flatMap { results ->
         if (noActiveTest(results)) Result.success(results) else {
            SpecExtensionsExecutor(registry)
               .afterSpec(spec)
               .map { results }
         }
      }
   }

   private fun noActiveTest(results: Map<TestCase, TestResult>): Boolean {
      return results.isEmpty() || results.all { it.value is TestResult.Ignored }
   }
}
