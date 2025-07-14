package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult

/**
 * Interceptors that are executed before a spec is instantiated.
 *
 * See [SpecInterceptor] for interceptors that are executed after a spec is instantiated.
 */
internal interface SpecRefInterceptor {
   suspend fun intercept(
      ref: SpecRef,
      next: NextSpecRefInterceptor,
   ): Result<Map<TestCase, TestResult>>
}

/**
 * Callback for invoking the next SpecRefInterceptor.
 *
 * This is a functional interface to reduce the size of stack traces - type-erased lambda types add excess stack lines.
 */
internal interface NextSpecRefInterceptor {
   suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>>
}
