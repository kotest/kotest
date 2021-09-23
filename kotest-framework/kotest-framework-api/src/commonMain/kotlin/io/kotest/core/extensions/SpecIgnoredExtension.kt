package io.kotest.core.extensions

import kotlin.reflect.KClass

/**
 * This listener is invoked if a spec was disabled and never executed.
 */
interface SpecIgnoredExtension {
   suspend fun ignored(kClass: KClass<*>, reason: String?)
}
