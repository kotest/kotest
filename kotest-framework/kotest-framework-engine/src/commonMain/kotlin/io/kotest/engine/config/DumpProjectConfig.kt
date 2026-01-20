package io.kotest.engine.config

import io.kotest.engine.TestEngineContext

/**
 * Outputs the resolved project configuration to the console if enabled.
 */
internal object DumpProjectConfig {

   fun dumpConfigIfEnabled(context: TestEngineContext) {
      try {
         if (context.projectConfigResolver.dumpConfig()) {
            val config = context.projectConfig
            if (config != null)
               AbstractProjectConfigWriter.dumpProjectConfig(config)
         }
      } catch (t: Throwable) {
         println("Error dumping project config: ${t.message}")
      }
   }
}
