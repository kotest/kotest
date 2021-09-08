package io.kotest.engine.concurrency

import io.kotest.core.config.configuration

actual val defaultCoroutineDispatcherController: CoroutineDispatcherController =
   ExecutorCoroutineDispatcherController(configuration.parallelism, configuration.dispatcherAffinity)

