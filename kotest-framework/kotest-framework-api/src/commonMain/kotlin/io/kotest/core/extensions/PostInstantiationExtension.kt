package io.kotest.core.extensions

import io.kotest.core.spec.Spec

/**
 * An extension point that is used to post-process a spec class after it has been instantiated
 * by Kotest using reflection at runtime.
 *
 * This extension allows the spec to be replaced with another instance of the same type, which can
 * be useful for proxying or dependency injection.
 *
 * Note: This extension invoked only when classes are created by Kotest and is therefore
 * restricted to JVM platforms. This restriction may be dropped in future releases.
 */
interface PostInstantiationExtension : Extension {

   /**
    * Accepts a [Spec] for post-processing.
    *
    * This function must return a spec to be used. Typically, the same instance is returned,
    * but the implementation is free to return another instance if desired.
    */
   fun process(spec: Spec): Spec
}
