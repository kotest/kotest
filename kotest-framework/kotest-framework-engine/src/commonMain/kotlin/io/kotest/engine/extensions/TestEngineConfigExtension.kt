package io.kotest.engine.extensions

interface TestEngineConfigExtension {
   fun transform(config: TestEngineConfig): TestEngineConfig
}
