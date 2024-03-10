package io.kotest.engine.spec.interceptor

import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptors.ClassVisibilitySpecRefInterceptor

internal actual fun platformInterceptors(context: EngineContext): List<SpecRefInterceptor> {
  return listOf(ClassVisibilitySpecRefInterceptor(context.configuration))
}
