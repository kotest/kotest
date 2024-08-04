package io.kotest.engine

import io.kotest.common.KotestInternal
import io.kotest.engine.test.interceptors.TestExecutionInterceptor

/**
 * Returns the [TestExecutionInterceptor]s that should be used for this platform.
 */
@KotestInternal
internal expect fun testInterceptorsForPlatform(): List<TestExecutionInterceptor>
