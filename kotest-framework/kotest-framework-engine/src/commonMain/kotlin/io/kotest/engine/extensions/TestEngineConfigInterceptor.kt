package io.kotest.engine.extensions

import io.kotest.common.KotestInternal
import io.kotest.engine.TestEngineConfig

/**
 * Internal interceptor that allows adjustment of the test engine config before
 * the [TestEngine] is created.
 */
@KotestInternal
internal interface TestEngineConfigInterceptor {
   fun process(config: TestEngineConfig): TestEngineConfig
}
