package io.kotest.engine.config

import io.kotest.engine.mapError
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.log
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object ConfigManager {

   /**
    * Initializes a given [ProjectConfiguration] instance using the supplied project configs,
    * system properties, autoscan, and detected project configs on the classpath.
    *
    * @return the initialized input
    */
   fun initialize(configuration: ProjectConfiguration, projectConfigs: () -> List<AbstractProjectConfig>): ProjectConfiguration {
      compile(configuration, projectConfigs).getOrThrow()
      return configuration
   }

   fun compile(configuration: ProjectConfiguration, projectConfigs: () -> List<AbstractProjectConfig>) = runCatching {
      log { "ConfigManager: compiling configs" }

      log { "ConfigManager: Applying platform defaults" }
      applyPlatformDefaults(configuration)

      log { "ConfigManager: Applying configs from system properties" }
      applyConfigFromSystemProperties(configuration)

      log { "ConfigManager: Applying configs from auto scan" }
      applyConfigFromAutoScan(configuration)

      log { "ConfigManager: Applying configs from compiled lamdas" }
      projectConfigs().forEach { applyConfigFromProjectConfig(it, configuration) }

   }.mapError { ConfigurationException(it) }
}

class ConfigurationException(cause: Throwable) : Exception(cause)

/**
 * Uses system properties to load configuration values onto the supplied [ProjectConfiguration] object.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
internal expect fun applyConfigFromSystemProperties(configuration: ProjectConfiguration)

/**
 * Modifies configuration with some defaults based on the platform.
 *
 * For example on JVM it will add System property based tag detection.
 */
internal expect fun applyPlatformDefaults(configuration: ProjectConfiguration)

/**
 *
 * Applies listeners, filters and extensions detected during scanning, that are annotated
 * with the [AutoScan] annotation.
 *
 * Note: This will only have an effect on JVM targets.
 */
internal expect fun applyConfigFromAutoScan(configuration: ProjectConfiguration)

/**
 * Scan the classpath for [AbstractProjectConfig] instances.
 *
 * Note: This will only have an effect on JVM targets.
 */
internal expect fun detectAbstractProjectConfigs(): List<AbstractProjectConfig>

