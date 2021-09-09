package io.kotest.engine.concurrency

import io.kotest.core.concurrency.CoroutineDispatcherFactory

actual fun defaultCoroutineDispatcherFactory(): CoroutineDispatcherFactory = NoopCoroutineDispatcherFactory
