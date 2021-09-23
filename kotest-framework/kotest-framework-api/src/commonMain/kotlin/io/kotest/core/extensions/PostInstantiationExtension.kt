package io.kotest.core.extensions

import io.kotest.core.spec.Spec

/**
 * An extension point that is used to post-process a spec class after it has been instantiated
 * by Kotest using reflection at runtime.
 *
 * This extension allows the spec to be replaced with another instance of the same type, which can
 * be useful for proxying or dependency injection.
 *
 * This extension is invoked regardless of whether the spec has any tests, or if the spec
 * was inactive. It is executed every single time a spec is created, so for isolation modes that result
 * in the same spec being instantiated multiple times, this extension will be invoked multiple times.
 *
 * Any changes to the coroutine context will not be propagated downstream.
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
   suspend fun instantiated(spec: Spec): Spec
}
