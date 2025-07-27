package io.kotest.engine.spec

import io.kotest.core.platform
import io.kotest.core.spec.Spec
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.extensions.ExtensionRegistry
import kotlin.reflect.KClass

actual suspend fun instantiate(
   kclass: KClass<*>,
   registry: ExtensionRegistry,
   projectConfigRegistry: ProjectConfigResolver
): Result<Spec> {
   return Result.failure(RuntimeException("Reflective instantiation is not supported on ${platform.name}"))
}
