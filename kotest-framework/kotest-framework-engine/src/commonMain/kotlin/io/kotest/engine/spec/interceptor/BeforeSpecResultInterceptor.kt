package io.kotest.engine.spec.interceptor

import io.kotest.common.flatMap
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.extensions.MultipleExceptions

/**
 * A [SpecInterceptor] that retrieves any before spec errors and returns those instead of test failures.
 */
internal object BeforeSpecResultInterceptor : SpecInterceptor {
   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return fn(spec).flatMap { results ->
         if (results.size == 1) {
            when (val t = results.values.first().errorOrNull) {
               is MultipleExceptions -> Result.failure(t)
               is ExtensionException.BeforeSpecException -> Result.failure(t)
               else -> Result.success(results)
            }
         } else Result.success(results)
      }
   }
}
