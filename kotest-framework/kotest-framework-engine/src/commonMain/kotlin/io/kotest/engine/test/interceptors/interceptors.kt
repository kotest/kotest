package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.ProjectConfiguration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark

/**
 * Returns a [TestExecutionInterceptor] for switching coroutine dispatchers.
 */
@JVMOnly
@ExperimentalStdlibApi
internal expect fun coroutineDispatcherFactoryInterceptor(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
): TestExecutionInterceptor

/**
 * Returns a [TestExecutionInterceptor] for switching execution to a dedicated thread
 * when blockingTest is true.
 */
@JVMOnly
@ExperimentalTime
internal expect fun blockedThreadTimeoutInterceptor(
   configuration: ProjectConfiguration,
   start: TimeMark,
): TestExecutionInterceptor

/**
 * Returns a [TestExecutionInterceptor] for keeping the error collector synchronized
 * with thread-switching coroutines.
 */
@JVMOnly
internal expect fun coroutineErrorCollectorInterceptor(): TestExecutionInterceptor
