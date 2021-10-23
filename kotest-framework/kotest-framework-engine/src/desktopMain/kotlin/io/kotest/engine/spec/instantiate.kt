package io.kotest.engine.spec

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

internal actual suspend fun instantiate(kclass: KClass<out Spec>, registry: ExtensionRegistry): Result<Spec> =
   Result.failure(RuntimeException("Reflective instantiation is not supported on kotlin/native"))
