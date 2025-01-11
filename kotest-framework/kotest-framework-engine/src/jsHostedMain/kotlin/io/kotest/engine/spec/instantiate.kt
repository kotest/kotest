package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.extensions.ExtensionRegistry
import kotlin.reflect.KClass

internal actual suspend fun instantiate(
   kclass: KClass<*>,
   registry: ExtensionRegistry,
   projectConfigRegistry: ProjectConfigResolver
): Result<Spec> =
   Result.failure(RuntimeException("Reflective instantiation is not supported on kotlin/js"))
