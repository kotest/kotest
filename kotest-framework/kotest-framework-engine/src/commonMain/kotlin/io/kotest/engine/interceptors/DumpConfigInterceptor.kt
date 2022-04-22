package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.engine.EngineResult
import io.kotest.engine.config.dumpProjectConfig
import io.kotest.mpp.syspropOrEnv

/**
 * Outputs a given [ProjectConfiguration] to the console.
 */
@OptIn(KotestInternal::class)
internal object DumpConfigInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {
      if (syspropEnabled()) {
         context.configuration.dumpProjectConfig()
      }
      return execute(context)
   }

   private fun syspropEnabled() =
      syspropOrEnv(KotestEngineProperties.dumpConfig) != null
}
