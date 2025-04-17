package io.kotest.engine.config

import io.kotest.common.JVMOnly
import io.kotest.common.KotestInternal
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.log
import io.kotest.engine.instantiateOrObject
import kotlin.reflect.KClass

/**
 * A [ProjectConfigLoader] is responsible for locating concrete implementations of [io.kotest.core.config.AbstractProjectConfig]
 * at runtime based on a well defined fully qualified class name using reflection lookups.
 */
@KotestInternal
@JVMOnly
object ProjectConfigLoader {

   const val DEFAULT_CONFIG_FQN = "io.kotest.provided.ProjectConfig"

   /**
    * Returns an [AbstractProjectConfig] instance if one is found on the classpath and loaded by reflection.
    */
   @Suppress("UNCHECKED_CAST")
   fun load(): AbstractProjectConfig? {
      val fqn = fqn()
      log { "Loading project configs from fqn: $fqn" }
      val kclass = runCatching { Class.forName(fqn).kotlin }.getOrNull() ?: return null
      return instantiateOrObject(kclass as KClass<out AbstractProjectConfig>).getOrThrow()
   }

   private fun fqn(): String {
      val fqn = System.getProperty(KotestEngineProperties.PROJECT_CONFIGURATION_FQN)
      return if (fqn == null) {
         log { "No project config class name provided, checking for default at $DEFAULT_CONFIG_FQN" }
         DEFAULT_CONFIG_FQN
      } else fqn
   }
}

