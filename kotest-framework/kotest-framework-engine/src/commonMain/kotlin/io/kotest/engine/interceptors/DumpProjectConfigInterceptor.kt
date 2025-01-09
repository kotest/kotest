package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult
import io.kotest.engine.config.AbstractProjectConfigWriter

/**
 * Outputs the resolved project configuration to the console.
 */
internal object DumpProjectConfigInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: NextEngineInterceptor,
   ): EngineResult {
      if (context.projectConfigResolver.dumpConfig()) {
         val config = context.projectConfig
         if (config != null)
            AbstractProjectConfigWriter.dumpProjectConfig(config)
      }
      return execute(context)
   }
}
