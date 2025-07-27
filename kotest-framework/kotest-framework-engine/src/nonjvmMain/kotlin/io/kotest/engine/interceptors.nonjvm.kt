package io.kotest.engine

import io.kotest.engine.test.interceptors.TestExecutionInterceptor

internal actual fun testInterceptorsForPlatform(): List<TestExecutionInterceptor> = emptyList()
