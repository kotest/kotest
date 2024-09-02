package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Interceptors that are executed after a spec is instantiated.
 */
internal interface SpecInterceptor {
   suspend fun intercept(
     spec: Spec,
     fn: NextSpecInterceptor
   ): Result<Map<TestCase, TestResult>>
}

/**
 * Callback for invoking the next SpecInterceptor.
 *
 * This is a functional interface to reduce the size of stack traces - type-erased lambda types add excess stack lines.
 */
internal fun interface NextSpecInterceptor {
   suspend operator fun invoke(spec: Spec): Result<Map<TestCase, TestResult>>
}
