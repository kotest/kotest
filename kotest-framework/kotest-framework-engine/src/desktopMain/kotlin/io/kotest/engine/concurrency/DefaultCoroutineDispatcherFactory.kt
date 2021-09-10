package io.kotest.engine.concurrency

import io.kotest.core.concurrency.CoroutineDispatcherFactory

internal actual fun defaultCoroutineDispatcherFactory(): CoroutineDispatcherFactory = NoopCoroutineDispatcherFactory
