package io.kotest.engine.spec.interceptor

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
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
      ref: SpecRef,
      next: NextSpecInterceptor,
   ): Result<Map<TestCase, TestResult>>
}

/**
 * The [SpecContext] is a context that can be used by [SpecInterceptor]s.
 * A fresh context is created for each spec instance.
 * It contains a mutable state that can be modified by the interceptors.
 */
@OptIn(ExperimentalAtomicApi::class)
internal data class SpecContext(
   // Set when a test fails and failfast is configured at the spec or project level (no enclosing
   // context with config.failfast=true). All subsequent tests with failfast enabled are skipped.
   var testFailed: Boolean = false,
   // The set of context descriptors whose failfast scope has been triggered by a test failure.
   // Used when failfast is configured at the context level rather than the spec level, so that
   // only tests within the failing context are skipped, not sibling contexts.
   val failedScopes: MutableSet<Descriptor.TestDescriptor> = mutableSetOf(),
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
