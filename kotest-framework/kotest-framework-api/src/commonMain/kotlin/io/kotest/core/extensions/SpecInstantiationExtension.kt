package io.kotest.core.extensions

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * An [Extension] point that enables hooking into the spec instantiation lifecycle.
 * Note: This is a JVM only extension.
 */
interface SpecInstantiationExtension : Extension {
   suspend fun onSpecInstantiation(spec: Spec) {}
   suspend fun onSpecInstantiationError(kclass: KClass<*>, t: Throwable) {}
}
