package io.kotest.engine.test.interceptors

import io.kotest.common.platform
import io.kotest.core.concurrency.CoroutineDispatcherFactory

internal actual fun coroutineDispatcherFactoryInterceptor(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
): TestExecutionInterceptor = error("Unsupported on $platform")

internal actual fun blockedThreadTimeoutInterceptor(): TestExecutionInterceptor =
   error("Unsupported on $platform")
