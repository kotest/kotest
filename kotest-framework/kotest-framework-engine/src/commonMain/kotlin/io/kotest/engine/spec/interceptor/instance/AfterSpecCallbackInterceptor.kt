package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.SpecInterceptor

/**
 * Invokes any [AfterSpecListener] callbacks for the given spec.
 */
internal class AfterSpecCallbackInterceptor(private val registry: ExtensionRegistry) : SpecInterceptor {
   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      val results = fn(spec)
      return SpecExtensions(registry)
         .afterSpec(spec)
         .fold(
            { results },
            { Result.failure(it) }
         )
   }
}
