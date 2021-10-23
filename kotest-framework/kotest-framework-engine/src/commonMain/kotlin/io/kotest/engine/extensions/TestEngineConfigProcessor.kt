package io.kotest.engine.extensions

import io.kotest.engine.TestEngineConfig

internal interface TestEngineConfigProcessor {
   fun process(config: TestEngineConfig): TestEngineConfig
}
