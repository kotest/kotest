package io.kotest.engine.interceptors

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.engine.EngineResult
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.config.dumpProjectConfig
import io.kotest.mpp.syspropOrEnv

/**
 * Outputs the resolved project configuration to the console.
 */
internal class DumpProjectConfigInterceptor(private val conf: AbstractProjectConfig) : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: NextEngineInterceptor
   ): EngineResult {
      if (syspropEnabled()) {
         dumpProjectConfig(conf)
      }
      return execute(context)
   }

   private fun syspropEnabled() =
      syspropOrEnv(KotestEngineProperties.dumpConfig).toBoolean()

}
