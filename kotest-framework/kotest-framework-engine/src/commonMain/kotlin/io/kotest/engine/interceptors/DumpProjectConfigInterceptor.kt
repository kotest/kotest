package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult
import io.kotest.engine.config.AbstractProjectConfigWriter
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.mpp.syspropOrEnv

/**
 * Outputs the resolved project configuration to the console.
 */
internal object DumpProjectConfigInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: NextEngineInterceptor,
   ): EngineResult {
      if (syspropEnabled()) {
         AbstractProjectConfigWriter.dumpProjectConfigIfNotNull(context.projectConfig)
      }
      return execute(context)
   }

   private fun syspropEnabled() =
      syspropOrEnv(KotestEngineProperties.dumpConfig).toBoolean()

}
