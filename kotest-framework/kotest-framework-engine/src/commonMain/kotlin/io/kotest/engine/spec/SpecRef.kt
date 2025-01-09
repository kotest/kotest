package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.engine.config.ProjectConfigResolver
import kotlin.reflect.KClass

/**
 * Returns an instance of the [Spec] represented by this [SpecRef].
 */
internal suspend fun SpecRef.instance(projectConfigRegistry: ProjectConfigResolver): Result<Spec> = when (this) {
   is SpecRef.Reference -> instantiate(this.kclass, projectConfigRegistry)
   is SpecRef.Singleton -> Result.success(this.instance)
   is SpecRef.Function -> Result.success(this.f())
}

internal expect suspend fun instantiate(kclass: KClass<*>, projectConfigRegistry: ProjectConfigResolver): Result<Spec>
