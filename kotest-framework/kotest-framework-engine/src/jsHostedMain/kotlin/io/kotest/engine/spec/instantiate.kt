package io.kotest.engine.spec

import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

internal actual suspend fun instantiate(kclass: KClass<*>, registry: ExtensionRegistry): Result<Spec> =
   Result.failure(RuntimeException("Reflective instantiation is not supported on kotlin/js"))
