package io.kotest.engine.extensions

import io.kotest.engine.TestEngineConfig

interface TestEngineConfigExtension {
   fun transform(config: TestEngineConfig): TestEngineConfig
}
