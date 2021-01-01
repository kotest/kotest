package io.kotest.engine.config

import io.kotest.core.config.configuration
import io.kotest.core.extensions.CoroutineDispatcherFactoryExtension
import io.kotest.engine.ExecutorCoroutineDispatcherFactory

val factory = lazy {
   configuration.extensions().filterIsInstance<CoroutineDispatcherFactoryExtension>().firstOrNull()
      ?: ExecutorCoroutineDispatcherFactory(configuration.parallelism)
}
