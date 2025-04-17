package io.kotest.engine.interceptors

import io.kotest.common.JVMOnly
import io.kotest.engine.EngineResult
import io.kotest.engine.config.ProjectConfigLoader

/**
 * An [EngineInterceptor] that loads an [io.kotest.core.config.AbstractProjectConfig] from the classpath
 * using a [io.kotest.engine.config.ProjectConfigLoader]. This replaces any config set programatically on the engine.
 */
@JVMOnly
internal object ProjectConfigurationEngineInterceptor : EngineInterceptor {
   override suspend fun intercept(
      context: EngineContext,
      execute: NextEngineInterceptor,
   ): EngineResult {
      val config = ProjectConfigLoader.load()
      return if (config == null)
         execute(context)
      else
         execute(context.withProjectConfig(config))
   }
}
