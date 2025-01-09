package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import io.kotest.engine.config.ProjectConfigResolver
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal actual suspend fun instantiate(kclass: KClass<*>, projectConfigRegistry: ProjectConfigResolver): Result<Spec> {
   return SpecInstantiator(projectConfigRegistry).createAndInitializeSpec(kclass as KClass<out Spec>)
}
