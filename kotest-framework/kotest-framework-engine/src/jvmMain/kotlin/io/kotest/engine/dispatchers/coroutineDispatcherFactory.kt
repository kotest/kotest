package io.kotest.engine.dispatchers

import io.kotest.core.config.configuration
import io.kotest.engine.extensions.CoroutineDispatcherFactoryExtension
import io.kotest.fp.firstOrNone
import io.kotest.fp.getOrElse

/**
 * Returns a [CoroutineDispatcherFactory] to allocate dispatchers to specs and tests.
 *
 * Will use a [CoroutineDispatcherFactoryExtension] if one is registered, otherwise will default
 * to the factory provided by [defaultCoroutineDispatcherFactory].
 */
fun coroutineDispatcherFactory(): CoroutineDispatcherFactory {
   return configuration.extensions().filterIsInstance<CoroutineDispatcherFactoryExtension>()
      .firstOrNone()
      .map { it.factory() }
      .getOrElse { defaultCoroutineDispatcherFactory() }
}

fun defaultCoroutineDispatcherFactory(): ExecutorCoroutineDispatcherFactory {
   return ExecutorCoroutineDispatcherFactory(configuration.parallelism, configuration.dispatcherAffinity)
}
