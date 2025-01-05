package io.kotest.engine.spec

import io.kotest.engine.config.ExtensionRegistry
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import kotlin.reflect.KClass

/**
 * Returns an instance of the [Spec] represented by this [SpecRef].
 *
 * @param registry used to notify listeners that are subscribed to spec creation events.
 */
internal suspend fun SpecRef.instance(registry: ExtensionRegistry): Result<Spec> = when (this) {
   is SpecRef.Reference -> instantiate(this.kclass, registry)
   is SpecRef.Singleton -> Result.success(this.instance)
   is SpecRef.Function -> Result.success(this.f())
}

internal expect suspend fun instantiate(kclass: KClass<*>, registry: ExtensionRegistry): Result<Spec>
