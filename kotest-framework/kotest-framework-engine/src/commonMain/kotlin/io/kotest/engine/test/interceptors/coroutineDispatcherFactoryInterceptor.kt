package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.concurrency.CoroutineDispatcherFactory

/**
 * Returns a [TestExecutionInterceptor] for switching coroutine dispatchers.
 *
 * On non-JVM platforms this returns a no-op.
 */
@JVMOnly
internal expect fun coroutineDispatcherFactoryInterceptor(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
): TestExecutionInterceptor
