package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.platform
import io.kotest.engine.config.TestConfigResolver
import kotlin.time.TimeMark

@JVMOnly
internal actual fun blockedThreadTimeoutInterceptor(
   configuration: ProjectConfiguration,
   start: TimeMark,
   testConfigResolver: TestConfigResolver,
): TestExecutionInterceptor = error("Unsupported on $platform")

@JVMOnly
internal actual fun coroutineErrorCollectorInterceptor(): TestExecutionInterceptor =
   error("Unsupported on $platform")
