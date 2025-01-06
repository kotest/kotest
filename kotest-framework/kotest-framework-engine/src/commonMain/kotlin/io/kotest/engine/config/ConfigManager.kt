package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.Extension
import io.kotest.core.log
import io.kotest.engine.mapError
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object ConfigManager {

   /**
    * Initializes a given [ProjectConfiguration] instance using the supplied project configs,
    * system properties, and detected project configs on the classpath.
    *
    * @return the initialized input
    */
   fun initialize(
      configuration: ProjectConfiguration,
      projectConfigs: () -> List<AbstractProjectConfig>
   ): ProjectConfiguration {
      compile(configuration, projectConfigs).getOrThrow()
      return configuration
   }

   fun compile(configuration: ProjectConfiguration, projectConfigs: () -> List<AbstractProjectConfig>) = runCatching {
      log { "ConfigManager: compiling configs" }

//      log { "ConfigManager: Applying platform defaults" }
//      loadPlatformDefaultExtensions(configuration)

//      log { "ConfigManager: Applying configs from system properties" }
//      applyConfigFromSystemProperties(configuration)

      log { "ConfigManager: Applying configs from compiled lamdas" }
      projectConfigs().forEach { applyConfigFromProjectConfig(it, configuration) }

   }.mapError { ConfigurationException(it) }
}

class ConfigurationException(cause: Throwable) : Exception(cause)

/**
 * Loads [Extension]s that should be applied automatically based on the platform.
 *
 * For example on JVM it will add System property based tag detection.
 */
internal expect fun loadPlatformDefaultExtensions(): List<Extension>

/**
 * Load an [AbstractProjectConfig] instance by using a well defined classname.
 * Only applies on the JVM.
 */
internal expect fun loadProjectConfigFromClassname(): AbstractProjectConfig?
