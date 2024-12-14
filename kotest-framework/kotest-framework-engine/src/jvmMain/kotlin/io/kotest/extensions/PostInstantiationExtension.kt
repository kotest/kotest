package io.kotest.extensions

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec

/**
 * An extension point that is used to post-process a spec class after it has been instantiated
 * by Kotest using reflection at runtime.
 *
 * This extension allows the spec to be replaced with another instance of the same type, or it
 * can be mutated, which can be useful for proxying or dependency injection.
 *
 * This extension is invoked immediately after a spec is instantiated. It is executed for every spec
 * instance, so for isolation modes that result in the same spec being instantiated multiple times,
 * this extension will be invoked multiple times.
 *
 * Note: This extension invoked only when classes are created via reflection on the JVM.
 * Therefore, this extension is useful when you only want to invoke logic on that platform.
 * For a cross-platform instantiation extension, see [SpecInitializeExtension].
 */
interface PostInstantiationExtension : Extension {

   /**
    * Accepts a [Spec] for post-processing.
    *
    * This function must return a spec to be used. Typically, the same instance is returned,
    * but the implementation is free to return another instance if desired.
    */
   suspend fun instantiated(spec: Spec): Spec
}
