package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Interceptors that are executed before a spec is instantiated.
 */
internal interface SpecRefInterceptor {
   suspend fun intercept(
      ref: SpecRef,
      fn: NextSpecRefInterceptor,
   ): Result<Map<TestCase, TestResult>>
}

/**
 * A functional interface for the interceptor callback, to reduce the size of stack traces.
 *
 * With a normal lambda type, each call adds three lines to the stacktrace, but an interface only adds one line.
 */
internal fun interface NextSpecRefInterceptor {
   suspend operator fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>>
}

