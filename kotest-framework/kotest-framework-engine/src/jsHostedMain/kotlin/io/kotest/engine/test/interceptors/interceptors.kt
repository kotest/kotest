package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.platform
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark

@ExperimentalTime
@JVMOnly
internal actual fun blockedThreadTimeoutInterceptor(
   configuration: ProjectConfiguration,
   start: TimeMark,
): TestExecutionInterceptor = error("Unsupported on $platform")

@JVMOnly
internal actual fun coroutineErrorCollectorInterceptor(): TestExecutionInterceptor =
   error("Unsupported on $platform")
