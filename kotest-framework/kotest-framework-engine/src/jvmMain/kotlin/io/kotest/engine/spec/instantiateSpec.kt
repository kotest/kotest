package io.kotest.engine.spec

import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal actual suspend fun instantiate(kclass: KClass<*>, registry: ExtensionRegistry): Result<Spec> {
   return SpecInstantiator(registry).createAndInitializeSpec(kclass as KClass<out Spec>)
}
