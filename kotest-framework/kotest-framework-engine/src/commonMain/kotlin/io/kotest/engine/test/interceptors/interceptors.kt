package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.engine.config.TestConfigResolver
import kotlin.time.TimeMark

/**
 * Returns a [TestExecutionInterceptor] for switching execution to a dedicated thread
 * when blockingTest is true.
 *
 * This is a JVM only function because it relies on the JVM thread model.
 */
@JVMOnly
internal expect fun blockedThreadTimeoutInterceptor(
   start: TimeMark,
   testConfigResolver: TestConfigResolver,
): TestExecutionInterceptor

/**
 * Returns a [TestExecutionInterceptor] for keeping the error collector synchronized
 * with thread-switching coroutines.
 */
@JVMOnly
internal expect fun coroutineErrorCollectorInterceptor(): TestExecutionInterceptor
