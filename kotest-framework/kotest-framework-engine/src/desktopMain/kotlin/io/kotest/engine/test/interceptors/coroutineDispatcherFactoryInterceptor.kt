package io.kotest.engine.test.interceptors

import io.kotest.core.concurrency.CoroutineDispatcherFactory

internal actual fun coroutineDispatcherFactoryInterceptor(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
): TestExecutionInterceptor = NoopTestExecutionInterceptor
