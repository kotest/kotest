package io.kotest.engine.interceptors

import io.kotest.core.config.Configuration
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.config.dumpProjectConfig
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.env
import io.kotest.mpp.sysprop

/**
 * Outputs a given [Configuration] to the console.
 */
internal class DumpConfigInterceptor(
   private val configuration: Configuration,
) : EngineInterceptor {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {
      if (sysprop(KotestEngineProperties.dumpConfig) != null || env(KotestEngineProperties.dumpConfig) != null) {
         configuration.dumpProjectConfig()
      }
      return execute(suite, listener)
   }
}
