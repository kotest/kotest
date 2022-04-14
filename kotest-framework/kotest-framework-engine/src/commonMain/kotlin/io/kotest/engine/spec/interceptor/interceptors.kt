package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Interceptors that are executed before a spec is instantiated.
 */
internal interface SpecRefInterceptor {
   suspend fun intercept(
      ref: SpecRefContainer,
      fn: suspend (SpecRefContainer) -> Result<Pair<SpecRefContainer, Map<TestCase, TestResult>>>,
   ): Result<Pair<SpecRefContainer, Map<TestCase, TestResult>>>
}

/**
 * Interceptors that are executed after a spec is instantiated.
 */
internal interface SpecInterceptor {
   suspend fun intercept(
      spec: SpecContainer,
      fn: suspend (SpecContainer) -> Result<Pair<SpecContainer, Map<TestCase, TestResult>>>,
   ): Result<Pair<SpecContainer, Map<TestCase, TestResult>>>
}

sealed class ActiveRootContainer {
   abstract val active: Boolean

}

sealed class SpecContainer : ActiveRootContainer() {
   abstract val spec: Spec
   abstract fun disable(): SpecContainer

}

sealed class SpecRefContainer : ActiveRootContainer() {
   abstract val specRef: SpecRef
   abstract fun disable(): SpecRefContainer
}

data class ActiveSpec(override val spec: Spec) : SpecContainer() {
   override val active = true
   override fun disable() = InactiveSpec(spec)
}

data class ActiveSpecRef(override val specRef: SpecRef) : SpecRefContainer() {
   override val active = true
   override fun disable()= InactiveSpecRef(specRef)
}

data class InactiveSpec internal constructor(override val spec: Spec) : SpecContainer() {
   override val active = false
   override fun disable() = this
}

data class InactiveSpecRef internal constructor(override val specRef: SpecRef) : SpecRefContainer() {
   override val active = false
   override fun disable() = this
}

