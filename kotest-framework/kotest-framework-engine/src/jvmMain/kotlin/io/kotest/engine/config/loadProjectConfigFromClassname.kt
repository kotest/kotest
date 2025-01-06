package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.log
import io.kotest.engine.instantiateOrObject
import kotlin.reflect.KClass

internal actual fun loadProjectConfigFromClassname(): AbstractProjectConfig? = ProjectConfigLoader.load()

internal object ProjectConfigLoader {

   const val DEFAULT_CONFIG_FQN = "io.kotest.provided.ProjectConfig"

   @Suppress("UNCHECKED_CAST")
   fun load(): AbstractProjectConfig? {
      val fqn = fqn()
      log { "Loading project configs from fqn: $fqn" }
      val kclass = runCatching { Class.forName(fqn).kotlin }.getOrNull() ?: return null
      return instantiateOrObject(kclass as KClass<out AbstractProjectConfig>).getOrThrow()
   }

   private fun fqn(): String {
      val fqn = System.getProperty(KotestEngineProperties.configurationClassNames)
      return if (fqn == null) {
         log { "No project config class name provided, checking for default at $DEFAULT_CONFIG_FQN" }
         DEFAULT_CONFIG_FQN
      } else fqn
   }
}

