package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.platform
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.ProjectConfiguration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark

@JVMOnly
internal actual fun coroutineDispatcherFactoryInterceptor(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
): TestExecutionInterceptor = error("Unsupported on $platform")

@ExperimentalTime
@JVMOnly
internal actual fun blockedThreadTimeoutInterceptor(
   configuration: ProjectConfiguration,
   start: TimeMark,
): TestExecutionInterceptor = error("Unsupported on $platform")

@JVMOnly
internal actual fun coroutineErrorCollectorInterceptor(): TestExecutionInterceptor =
   error("Unsupported on $platform")
