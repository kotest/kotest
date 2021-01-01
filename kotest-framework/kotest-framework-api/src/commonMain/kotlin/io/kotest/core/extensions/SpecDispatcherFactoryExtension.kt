package io.kotest.core.extensions

import io.kotest.core.spec.Spec
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.reflect.KClass

/**
 * Extension that can control the assignment of [CoroutineDispatcher]s to specs.
 * If multiple instances of this extension are provided, then an arbitrary one is picked.
 */
interface SpecDispatcherFactoryExtension {

   /**
    * Called to retrive a [CoroutineDispatcher] for the given spec.
    *
    * Can return the same dispatcher for multiple specs or all specs, or can return
    * a new dispatcher every time, or some combination.
    */
   fun dispatcherFor(spec: KClass<out Spec>): CoroutineDispatcher

   /**
    * Invoked once all specs have completed. Extensions can use this function to clean
    * up any threads or other resources they created.
    */
   fun stop()
}

