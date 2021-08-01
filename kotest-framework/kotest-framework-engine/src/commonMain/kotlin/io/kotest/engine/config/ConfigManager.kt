package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.Configuration

object ConfigManager {

   /**
    * Initializes a given [Configuration] instance using project config, system properties and autoscan.
    *
    * @return the initialized input
    */
   fun initialize(configuration: Configuration, projectConfigs: List<AbstractProjectConfig>): Configuration {
      applyPlatformDefaults(configuration)
      applyConfigFromSystemProperties(configuration)
      applyConfigFromAutoScan(configuration)
      projectConfigs.forEach { applyConfigFromProjectConfig(it, configuration) }
      return configuration
   }
}

/**
 * Uses system properties to load configuration values onto the supplied [Configuration] object.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
expect fun applyConfigFromSystemProperties(configuration: Configuration)

/**
 * Modifies configuration with some defaults based on the platform.
 *
 * For example on JVM it will add System property based tag detection.
 */
expect fun applyPlatformDefaults(configuration: Configuration)

/**
 *
 * Applies listeners, filters and extensions detected during scanning, that are annotated
 * with the [AutoScan] annotation.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
expect fun applyConfigFromAutoScan(configuration: Configuration)
