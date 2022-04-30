package io.kotest.engine.test.interceptors

/**
 * A [TestExecutionInterceptor] that uses [runTest] from the coroutine library
 * to install test dispatchers.
 *
 * This setting cannot be nested.
 */
expect class TestCoroutineInterceptor() : TestExecutionInterceptor
