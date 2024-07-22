package io.kotest.engine.spec.interceptor

import io.kotest.engine.interceptors.EngineContext

internal actual fun platformInterceptors(context: EngineContext): List<SpecRefInterceptor> = emptyList()
