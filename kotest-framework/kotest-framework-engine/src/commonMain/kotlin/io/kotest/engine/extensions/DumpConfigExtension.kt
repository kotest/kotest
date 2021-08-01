package io.kotest.engine.extensions

import io.kotest.core.config.Configuration
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.config.dumpProjectConfig
import io.kotest.engine.listener.TestEngineListener

/**
 * Outputs a given [Configuration] to the console.
 */
internal class DumpConfigExtension(
   private val configuration: Configuration,
) : EngineExtension {

   override fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {
      configuration.dumpProjectConfig()
      return execute(suite, listener)
   }
}
