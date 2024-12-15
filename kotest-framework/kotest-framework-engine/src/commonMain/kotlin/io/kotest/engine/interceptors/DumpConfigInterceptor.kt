package io.kotest.engine.interceptors

import io.kotest.core.config.ProjectConfiguration
import io.kotest.engine.EngineResult
import io.kotest.engine.KotestEngineProperties
import io.kotest.engine.config.dumpProjectConfig
import io.kotest.mpp.syspropOrEnv

/**
 * Outputs a given [ProjectConfiguration] to the console.
 */
internal object DumpConfigInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: NextEngineInterceptor
   ): EngineResult {
      if (syspropEnabled()) {
         context.configuration.dumpProjectConfig()
      }
      return execute(context)
   }

   private fun syspropEnabled() =
      syspropOrEnv(KotestEngineProperties.dumpConfig).toBoolean()

}
