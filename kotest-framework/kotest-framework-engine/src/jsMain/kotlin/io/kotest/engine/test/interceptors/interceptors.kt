package io.kotest.engine.test.interceptors

import io.kotest.common.platform
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import kotlin.time.TimeMark

internal actual fun coroutineDispatcherFactoryInterceptor(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
): TestExecutionInterceptor = error("Unsupported on $platform")

internal actual fun blockedThreadTimeoutInterceptor(
   configuration: Configuration,
   start: TimeMark,
): TestExecutionInterceptor = error("Unsupported on $platform")

internal actual fun coroutineErrorCollectorInterceptor(): TestExecutionInterceptor =
   error("Unsupported on $platform")
