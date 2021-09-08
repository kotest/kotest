package io.kotest.engine.concurrency

import io.kotest.core.config.configuration

actual fun defaultCoroutineDispatcherController(): CoroutineDispatcherController =
   ExecutorCoroutineDispatcherController(configuration.parallelism, configuration.dispatcherAffinity)

