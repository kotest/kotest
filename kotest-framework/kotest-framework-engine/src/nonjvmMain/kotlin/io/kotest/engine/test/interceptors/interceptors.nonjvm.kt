package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.common.platformExecution
import io.kotest.engine.config.TestConfigResolver
import kotlin.time.TimeMark

@JVMOnly
internal actual fun blockedThreadTimeoutInterceptor(
   start: TimeMark,
   testConfigResolver: TestConfigResolver,
): TestExecutionInterceptor = error("Unsupported on ${platformExecution.platform}")

@JVMOnly
internal actual fun coroutineErrorCollectorInterceptor(): TestExecutionInterceptor =
   error("Unsupported on ${platformExecution.platform}")
