package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.concurrency.CoroutineDispatcherFactory

/**
 * Returns a [TestExecutionInterceptor] for switching coroutine dispatchers.
 */
@JVMOnly
internal expect fun coroutineDispatcherFactoryInterceptor(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
): TestExecutionInterceptor

/**
 * Returns a [TestExecutionInterceptor] for switching execution to a dedicated thread
 * when blockingTest is true.
 */
@JVMOnly
internal expect fun blockedThreadTimeoutInterceptor(): TestExecutionInterceptor
