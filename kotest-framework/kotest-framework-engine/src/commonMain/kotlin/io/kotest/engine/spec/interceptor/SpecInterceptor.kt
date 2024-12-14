package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.atomic.AtomicBoolean
import io.kotest.engine.atomic.createAtomicBoolean
import kotlin.coroutines.CoroutineContext

/**
 * Interceptors that are executed after a spec is instantiated.
 */
internal interface SpecInterceptor {
   suspend fun intercept(
      spec: Spec,
      next: NextSpecInterceptor,
   ): Result<Map<TestCase, TestResult>>
}

/**
 * The [SpecContext] is a context that can be used by [SpecInterceptor]s.
 * A fresh context is created for each spec instance.
 * It contains mutable state that can be modified by the interceptors.
 */
data class SpecContext(
   val specCoroutineContext: CoroutineContext,
   val beforeSpecInvoked: AtomicBoolean,
   var beforeSpecError: Throwable? = null,
) {
   companion object {
      fun create(specCoroutineContext: CoroutineContext) = SpecContext(
         specCoroutineContext = specCoroutineContext,
         beforeSpecInvoked = createAtomicBoolean(false),
         beforeSpecError = null,
      )
   }
}

/**
 * Callback for invoking the next SpecInterceptor.
 *
 * This is a functional interface to reduce the size of stack traces - type-erased lambda types add excess stack lines.
 */
internal interface NextSpecInterceptor {
   suspend fun invoke(spec: Spec): Result<Map<TestCase, TestResult>>
}
