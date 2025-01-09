package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import io.kotest.engine.config.ProjectConfigResolver
import kotlin.reflect.KClass

internal actual suspend fun instantiate(kclass: KClass<*>, projectConfigRegistry: ProjectConfigResolver): Result<Spec> =
   Result.failure(RuntimeException("Reflective instantiation is not supported on kotlin/js"))
