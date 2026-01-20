package io.kotest.engine.test.interceptors

/**
 * Returns the [TestExecutionInterceptor]s that should be used for this platform.
 */
internal expect fun testInterceptorsForPlatform(): List<TestExecutionInterceptor>
