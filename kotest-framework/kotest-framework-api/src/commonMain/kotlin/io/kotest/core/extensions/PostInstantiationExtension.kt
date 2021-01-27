package io.kotest.core.extensions

import io.kotest.core.spec.Spec

/**
 * An extension point that is used to post-process a spec class after it has been instantiated.
 *
 * This extension is useful for dependency injection or for invoking initialization logic.
 */
interface PostInstantiationExtension : Extension {

   /**
    * Accepts a [Spec] for post processing.
    *
    * This function must return a spec to be used. Typically, the same instance is returned,
    * but the implementation is free to return another instance if desired.
    */
   fun process(spec: Spec): Spec
}
