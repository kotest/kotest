package io.kotest.engine.test.interceptors

import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Returns a [TestExecutionInterceptor].
 */
@ExperimentalCoroutinesApi
@ExperimentalStdlibApi
internal actual fun createTestDispatcherInterceptor(): TestExecutionInterceptor = TestDispatcherInterceptor()
