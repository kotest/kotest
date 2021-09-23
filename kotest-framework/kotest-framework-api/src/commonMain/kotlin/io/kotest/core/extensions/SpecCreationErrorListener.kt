package io.kotest.core.extensions

import kotlin.reflect.KClass

interface SpecCreationErrorListener : Extension {
   suspend fun onSpecCreationError(kclass: KClass<*>, t: Throwable)
}
