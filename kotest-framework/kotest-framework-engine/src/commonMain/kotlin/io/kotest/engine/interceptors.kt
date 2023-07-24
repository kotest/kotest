package io.kotest.engine

import io.kotest.common.KotestInternal
import io.kotest.engine.spec.interceptor.SpecInterceptor
import io.kotest.engine.test.interceptors.TestExecutionInterceptor

/**
 * Returns the [SpecInterceptor]s that should be used for this platform.
 */
@KotestInternal
internal expect fun specInterceptorsForPlatform(): List<SpecInterceptor>

/**
 * Returns the [TestExecutionInterceptor]s that should be used for this platform.
 */
@KotestInternal
internal expect fun testInterceptorsForPlatform(): List<TestExecutionInterceptor>
