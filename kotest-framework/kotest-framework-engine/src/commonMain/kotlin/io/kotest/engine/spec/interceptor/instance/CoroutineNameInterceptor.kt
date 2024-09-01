package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.interceptor.SpecInterceptor
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.withContext

/**
 * A [SpecInterceptor] that adds a [CoroutineName] to this coroutine's context so we can test
 * that we're running callbacks in the right context.
 */
internal object CoroutineNameInterceptor : SpecInterceptor {
   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return withContext(CoroutineName("kotest-spec-${spec::class.simpleName}")) {
         fn(spec)
      }
   }
}
