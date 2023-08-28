package io.kotest.engine.spec.interceptor.instance

import io.kotest.common.flatMap
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.SpecInterceptor
import io.kotest.engine.spec.interceptor.ref.BeforeSpecState
import io.kotest.engine.spec.interceptor.ref.beforeSpecStateKey

/**
 * Invokes any [AfterSpecListener] callbacks for the given spec.
 */
internal class AfterSpecCallbackInterceptor(private val registry: ExtensionRegistry) : SpecInterceptor {
   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      val results = fn(spec)
      return SpecExtensions(registry)
         .afterSpec(spec)
         .fold(
            { results },
            { Result.failure(it) }
         )
   }
}

/**
 * Executes once a spec has completed, and checks for the presence of failed before spec listeners.
 * If so, will override the results.
 */
internal class BeforeSpecListenerSpecInterceptor(private val context: EngineContext) : SpecInterceptor {
   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      val state = context.state[spec::class.beforeSpecStateKey()] as? BeforeSpecState
      return fn(spec).flatMap { results ->
         when {
            state == null -> Result.success(results)
            state.errors.isEmpty() -> Result.success(results)
            state.errors.size == 1 -> Result.failure(state.errors.single())
            else -> Result.failure(MultipleExceptions(state.errors))
         }
      }
   }
}
