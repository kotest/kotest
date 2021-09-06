package io.kotest.core.listeners

import kotlin.reflect.KClass

/**
 * This listener is invoked if a spec was disabled and never executed.
 */
interface IgnoredSpecListener {
   fun specIgnored(kClass: KClass<*>, reason: String?)
}
