package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.extensions.ExtensionRegistry
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal actual suspend fun instantiate(
   kclass: KClass<*>,
   registry: ExtensionRegistry,
   projectConfigRegistry: ProjectConfigResolver
): Result<Spec> {
   return SpecInstantiator(registry, projectConfigRegistry).createAndInitializeSpec(kclass as KClass<out Spec>)
}
