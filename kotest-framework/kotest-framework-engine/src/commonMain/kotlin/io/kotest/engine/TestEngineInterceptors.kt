package io.kotest.engine

import io.kotest.common.KotestInternal
import io.kotest.core.config.Configuration
import io.kotest.engine.interceptors.EngineInterceptor

/**
 * Returns the [EngineInterceptor]s that should be used for this platform.
 */
@KotestInternal
internal expect fun testEngineInterceptors(conf: Configuration): List<EngineInterceptor>
