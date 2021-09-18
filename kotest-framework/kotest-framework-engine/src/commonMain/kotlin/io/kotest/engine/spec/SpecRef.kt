package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import kotlin.reflect.KClass

data class ReflectiveSpecRef(override val kclass: KClass<out Spec>) : SpecRef {
   override fun instance(): Result<Spec> = instantiate(kclass)
}

data class FunctionSpecRef(val fn: () -> Spec, override val kclass: KClass<out Spec>) : SpecRef {
   override fun instance(): Result<Spec> = Result.success(fn())
}

data class InstanceSpecRef(val spec: Spec) : SpecRef {
   override val kclass: KClass<out Spec> = spec::class
   override fun instance(): Result<Spec> = Result.success(spec)
}

internal expect fun instantiate(kclass: KClass<out Spec>): Result<Spec>
