package io.kotest.engine.concurrency

actual fun defaultCoroutineDispatcherController(): CoroutineDispatcherController = NoopCoroutineDispatcherController
