package io.kotest.engine.spec.interceptor.ref

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.engine.test.interceptors.BeforeSpecListenerInterceptor
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Configures a [BeforeSpecState] instance that the [BeforeSpecListenerInterceptor]
 * can use to report the status of before spec callbacks, which are invoked lazily
 * when the first test in a spec is executed.
 */
internal class BeforeSpecStateInterceptor(private val context: EngineContext) : SpecRefInterceptor {
   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      val state = BeforeSpecState(mutableListOf(), mutableSetOf(), mutableSetOf())
      context.state[ref.kclass.beforeSpecStateKey()] = state
      return next.invoke(ref)
   }
}

internal fun KClass<*>.beforeSpecStateKey() = "before_spec_" + this::class.bestName()

internal data class BeforeSpecState(
   val errors: MutableList<Throwable>,
   val success: MutableSet<Spec>,
   val failed: MutableSet<Spec>,
)
