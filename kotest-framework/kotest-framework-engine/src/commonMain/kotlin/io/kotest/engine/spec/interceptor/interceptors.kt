package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Interceptors that are executed before a spec is instantiated.
 */
internal interface SpecRefInterceptor {
   suspend fun intercept(fn: suspend (SpecRef) -> Map<TestCase, TestResult>): suspend (SpecRef) -> Map<TestCase, TestResult>
}

/**
 * Interceptors that are executed after a spec is instantiated.
 */
internal interface SpecInterceptor {
   suspend fun intercept(fn: suspend (Spec) -> Map<TestCase, TestResult>): suspend (Spec) -> Map<TestCase, TestResult>
}
