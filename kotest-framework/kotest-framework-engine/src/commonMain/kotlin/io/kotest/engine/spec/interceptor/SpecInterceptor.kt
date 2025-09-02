package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Interceptors that are executed after a spec is instantiated.
 *
 * See [SpecRefInterceptor] for interceptors that are executed before a spec is instantiated.
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
@OptIn(ExperimentalAtomicApi::class)
internal data class SpecContext(
   var testFailed: Boolean = false,
) {
   companion object {
      fun create() = SpecContext()
   }
}

/**
 * Callback for invoking the next SpecInterceptor.
 *
 * This is a functional interface to reduce the size of stack traces - type-erased lambda types add excess stack lines.
 */
internal fun interface NextSpecInterceptor {
   suspend fun invoke(spec: Spec): Result<Map<TestCase, TestResult>>
}
