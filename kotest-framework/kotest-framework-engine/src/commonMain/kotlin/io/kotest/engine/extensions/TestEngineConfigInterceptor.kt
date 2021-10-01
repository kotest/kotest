package io.kotest.engine.extensions

import io.kotest.engine.TestEngineConfig

internal interface TestEngineConfigInterceptor {
   fun intercept(config: TestEngineConfig): TestEngineConfig
}
