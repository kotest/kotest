package io.kotest.engine.interceptors

import io.kotest.common.JVMOnly
import io.kotest.engine.EngineResult
import io.kotest.engine.config.KotestPropertiesLoader

/**
 * An [EngineInterceptor] that sets system properties before the engine starts by loading
 * a properties file from the classpath.
 */
@JVMOnly
internal object SystemPropertiesEngineInterceptor : EngineInterceptor {
   override suspend fun intercept(
      context: EngineContext,
      execute: NextEngineInterceptor,
   ): EngineResult {
      KotestPropertiesLoader.loadAndApplySystemPropsFile()
      return execute(context)
   }
}

