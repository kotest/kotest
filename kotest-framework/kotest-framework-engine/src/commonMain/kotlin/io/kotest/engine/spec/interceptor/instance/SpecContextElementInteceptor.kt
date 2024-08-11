package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.interceptor.SpecInterceptor
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * A [SpecInterceptor] that adds a [SpecContextElement] to this coroutine's context so later
 * we can get access to the spec level coroutine.
 */
internal object SpecContextElementInteceptor : SpecInterceptor {
   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return withContext(SpecContextElement(coroutineContext)) {
         fn(spec)
      }
   }
}

internal class SpecContextElement(
   val context: CoroutineContext,
) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<SpecContextElement>
}

