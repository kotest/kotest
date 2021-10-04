package io.kotest.core.extensions

import kotlin.reflect.KClass

/**
 * This listener is invoked if a spec was disabled and never executed.
 */
interface SpecIgnoredListener {
   suspend fun ignoredSpec(kclass: KClass<*>, reason: String?)
}
