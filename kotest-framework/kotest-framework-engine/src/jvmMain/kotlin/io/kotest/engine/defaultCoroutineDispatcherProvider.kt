package io.kotest.engine

import io.kotest.core.config.configuration

actual val defaultCoroutineDispatcherProvider: CoroutineDispatcherProvider =
   ExecutorCoroutineDispatcherProvider(configuration.parallelism, configuration.dispatcherAffinity)
