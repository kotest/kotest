package io.kotest.engine.extensions

import io.kotest.common.ExperimentalKotest
import io.kotest.engine.CoroutineDispatcherProvider

/**
 * An extension point that can be used to return a [CoroutineDispatcherProvider] which
 * is then used to provide coroutine dispatchers for specs and tests.
 *
 * If this is not implemented then the Kotest engine will use a default implementation.
 * If two or more instances of this extension are used, then one will be arbitrarily picked.
 */
@ExperimentalKotest
interface CoroutineDispatcherExtension {
   fun provider(): CoroutineDispatcherProvider
}

