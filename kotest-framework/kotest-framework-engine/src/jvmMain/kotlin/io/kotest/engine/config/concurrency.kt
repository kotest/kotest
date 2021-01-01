package io.kotest.engine.config

import io.kotest.core.config.configuration
import io.kotest.core.extensions.CoroutineDispatcherFactoryExtension
import io.kotest.engine.DefaultSpecDispatcherFactory

val factory = lazy {
   configuration.extensions().filterIsInstance<CoroutineDispatcherFactoryExtension>().firstOrNull()
      ?: DefaultSpecDispatcherFactory(configuration.parallelism)
}
