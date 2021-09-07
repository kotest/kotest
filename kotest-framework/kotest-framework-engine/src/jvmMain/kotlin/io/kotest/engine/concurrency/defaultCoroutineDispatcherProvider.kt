package io.kotest.engine.concurrency

import io.kotest.core.config.configuration

actual val defaultCoroutineDispatcherProvider: CoroutineDispatcherAssignment =
   ExecutorCoroutineDispatcherAssignment(configuration.parallelism, configuration.dispatcherAffinity)

