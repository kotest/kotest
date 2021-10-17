package io.kotest.core.listeners

import kotlin.reflect.KClass

/**
 * This listener is invoked if a spec was never instantiated.
 */
interface IgnoredSpecListener {
   suspend fun ignoredSpec(kclass: KClass<*>, reason: String?)
}
