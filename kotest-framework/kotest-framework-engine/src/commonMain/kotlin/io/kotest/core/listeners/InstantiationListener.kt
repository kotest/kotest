package io.kotest.core.listeners

import io.kotest.core.spec.Spec

/**
 * An extension point that is used to be notified when a spec is instantiated.
 */
interface InstantiationListener {

   /**
    * Is notified of a [Spec] that has been created.
    */
   suspend fun specInstantiated(spec: Spec)
}
