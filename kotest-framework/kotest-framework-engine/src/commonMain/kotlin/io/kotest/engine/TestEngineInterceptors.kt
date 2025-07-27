package io.kotest.engine

import io.kotest.engine.interceptors.EngineInterceptor

/**
 * Returns the [EngineInterceptor]s that should be used for this platform.
 */
internal expect fun testEngineInterceptorsForPlatform(): List<EngineInterceptor>
