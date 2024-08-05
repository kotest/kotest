package io.kotest.engine.test.interceptors

import io.kotest.core.platform

/**
 * Returns a [TestExecutionInterceptor].
 */
internal actual fun createTestDispatcherInterceptor(): TestExecutionInterceptor = error("Unsupported on $platform")
