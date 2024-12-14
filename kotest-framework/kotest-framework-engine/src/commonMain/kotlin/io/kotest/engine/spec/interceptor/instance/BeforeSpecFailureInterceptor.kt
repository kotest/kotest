package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.flatMap
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.spec.interceptor.SpecInterceptor

/**
 * Executes after a spec has completed, and checks for the presence of `beforeSpec` errors.
 * In the case of an error, this interceptor will override the failure message.
 */
internal class BeforeSpecFailureInterceptor(private val specContext: SpecContext) : SpecInterceptor {
   override suspend fun intercept(spec: Spec, next: NextSpecInterceptor): Result<Map<TestCase, TestResult>> {
      return next.invoke(spec).flatMap { results ->
         when (val error = specContext.beforeSpecError) {
            null -> Result.success(results)// no change
            else -> Result.failure(error)
         }
      }
   }
}
