package io.kotest.engine.test.interceptors

import io.kotest.core.platform

internal actual fun createTestDispatcherInterceptor(): TestExecutionInterceptor = error("Unsupported on $platform")
