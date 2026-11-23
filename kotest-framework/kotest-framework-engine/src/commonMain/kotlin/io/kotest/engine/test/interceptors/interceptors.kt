package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.TestCaseExecutionListener
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

/**
 * Returns a [TestExecutionInterceptor] that makes the test available through `CurrentTestCase` on every
 * thread its coroutines run on, and captures its output if enabled, or null on platforms without threads
 * to track.
 */
@JVMOnly
internal expect fun currentTestCaseInterceptor(
   listener: TestCaseExecutionListener,
   projectConfigResolver: ProjectConfigResolver,
   testConfigResolver: TestConfigResolver,
): TestExecutionInterceptor?
