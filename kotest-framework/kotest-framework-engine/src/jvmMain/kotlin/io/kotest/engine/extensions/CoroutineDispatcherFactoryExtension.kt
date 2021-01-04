package io.kotest.engine.extensions

import io.kotest.core.config.ExperimentalKotest
import io.kotest.core.extensions.Extension
import io.kotest.engine.dispatchers.CoroutineDispatcherFactory

/**
 * An extension point that can be used to return a custom [CoroutineDispatcherFactory].
 */
@ExperimentalKotest
interface CoroutineDispatcherFactoryExtension : Extension {
   fun factory(): CoroutineDispatcherFactory
}
