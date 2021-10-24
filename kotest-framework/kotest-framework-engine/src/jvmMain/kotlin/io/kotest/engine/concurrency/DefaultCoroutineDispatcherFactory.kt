package io.kotest.engine.concurrency

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration

internal actual fun defaultCoroutineDispatcherFactory(configuration: Configuration): CoroutineDispatcherFactory =
   FixedThreadCoroutineDispatcherFactory(configuration.parallelism, configuration.dispatcherAffinity)
