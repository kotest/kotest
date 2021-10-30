package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import kotlin.reflect.KClass

@ExperimentalKotest
data class ReflectiveSpecRef(override val kclass: KClass<out Spec>) : SpecRef {
   override suspend fun instance(registry: ExtensionRegistry): Result<Spec> = instantiate(kclass, registry)
}

@ExperimentalKotest
data class InstanceSpecRef(val spec: Spec) : SpecRef {
   override val kclass: KClass<out Spec> = spec::class
   override suspend fun instance(registry: ExtensionRegistry): Result<Spec> = Result.success(spec)
}

internal expect suspend fun instantiate(kclass: KClass<*>, registry: ExtensionRegistry): Result<Spec>
