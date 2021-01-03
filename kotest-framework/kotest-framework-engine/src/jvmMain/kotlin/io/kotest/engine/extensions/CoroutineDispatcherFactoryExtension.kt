package io.kotest.engine.extensions

import io.kotest.engine.dispatchers.CoroutineDispatcherFactory

/**
 * An extension point that can be used to return a custom [CoroutineDispatcherFactory].
 */
interface CoroutineDispatcherFactoryExtension {
   fun factory(): CoroutineDispatcherFactory
}
