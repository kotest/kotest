package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.extensions.ExtensionRegistry
import kotlin.reflect.KClass

/**
 * Returns an instance of the [Spec] represented by this [SpecRef].
 */
internal suspend fun SpecRef.instance(
   registry: ExtensionRegistry,
   projectConfigRegistry: ProjectConfigResolver
): Result<Spec> = when (this) {
   is SpecRef.Reference -> instantiate(this.kclass, registry, projectConfigRegistry)
   is SpecRef.Function -> Result.success(this.f())
}

internal expect suspend fun instantiate(
   kclass: KClass<*>,
   registry: ExtensionRegistry,
   projectConfigRegistry: ProjectConfigResolver
): Result<Spec>
