package io.kotest.core

import io.kotest.core.spec.AutoCloseable

/**
 * Defines functions that allow for instances of [AutoCloseable] to registered with a [Spec]
 * or [TestFactory] to automatically call .close() on completion of all tests within that spec.
 */
interface AutoClosing {

   /**
    * Returns the [AutoCloseable] instances that have been registered with this spec or test factory.
    */
   fun autocloseables(): List<Lazy<AutoCloseable>>

   /**
    * Registers an [AutoCloseable] to be closed when the spec is completed.
    */
   fun <T : AutoCloseable> autoClose(closeable: T): T

   /**
    * Registers a lazy [AutoCloseable] to be closed when the spec is completed.
    */
   fun <T : AutoCloseable> autoClose(closeable: Lazy<T>): Lazy<T>

}
