package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

internal actual fun instantiate(kclass: KClass<out Spec>): Result<Spec> =
   Result.failure(RuntimeException("Reflective instantiation is not supported on kotlin/js"))
