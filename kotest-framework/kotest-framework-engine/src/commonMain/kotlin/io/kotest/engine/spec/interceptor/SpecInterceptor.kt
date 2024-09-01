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
 * A functional interface for the interceptor callback, to reduce the size of stack traces.
 *
 * With a normal lambda type, each call adds three lines to the stacktrace, but an interface only adds one line.
 */
internal fun interface NextSpecInterceptor {
   suspend operator fun invoke(spec: Spec): Result<Map<TestCase, TestResult>>
}
