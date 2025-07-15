package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.atomic.AtomicBoolean
import io.kotest.engine.atomic.createAtomicBoolean

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
internal data class SpecContext(
   val beforeSpecInvoked: AtomicBoolean,
   var beforeSpecError: Throwable? = null,
) {
   companion object {
      fun create() = SpecContext(createAtomicBoolean(false), null)
   }
}

/**
 * The [ContainerContext] is a context that can be used by [TestInterceptor]s to share state between tests
 * inside a common container (eg a context or describe block).
 */
internal data class ContainerContext(
   var testFailed: Boolean,
) {
   companion object {
      fun create() = ContainerContext(false)
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
