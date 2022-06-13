package io.kotest.engine.test.interceptors

import io.kotest.common.TimeMarkCompat
import io.kotest.common.platform
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.ProjectConfiguration

internal actual fun coroutineDispatcherFactoryInterceptor(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
): TestExecutionInterceptor = error("Unsupported on $platform")

internal actual fun blockedThreadTimeoutInterceptor(
   configuration: ProjectConfiguration,
   start: TimeMarkCompat,
): TestExecutionInterceptor = error("Unsupported on $platform")

internal actual fun coroutineErrorCollectorInterceptor(): TestExecutionInterceptor =
   error("Unsupported on $platform")
