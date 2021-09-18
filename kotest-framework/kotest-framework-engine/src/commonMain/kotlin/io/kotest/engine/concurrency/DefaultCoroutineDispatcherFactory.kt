package io.kotest.engine.concurrency

import io.kotest.core.concurrency.CoroutineDispatcherFactory

/**
 * Returns the default [CoroutineDispatcherFactory] used unless overriden in configuration
 * or per spec.
 */
internal expect fun defaultCoroutineDispatcherFactory(): CoroutineDispatcherFactory
