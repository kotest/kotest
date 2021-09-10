package io.kotest.engine.concurrency

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.configuration

internal actual fun defaultCoroutineDispatcherFactory(): CoroutineDispatcherFactory =
   FixedThreadCoroutineDispatcherFactory(configuration.parallelism, configuration.dispatcherAffinity)
