package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import kotlin.reflect.KClass

interface InstantiationErrorListener : Extension {
   suspend fun instantiationError(kclass: KClass<*>, t: Throwable)
}
