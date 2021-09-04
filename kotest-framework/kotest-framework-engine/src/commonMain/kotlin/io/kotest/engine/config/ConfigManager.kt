package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.Configuration
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object ConfigManager {

   // since we are saddled with the global configuration singleton (for now at least)
   // we need to make sure we don't initialize it twice

   private var initialized = false

   /**
    * Initializes a given [Configuration] instance using the supplied project configs,
    * system properties, autoscan, and project configs on the classpath.
    *
    * @return the initialized input
    */
   fun initialize(configuration: Configuration, projectConfigs: List<AbstractProjectConfig>): Configuration {
      if (!initialized) {
         applyPlatformDefaults(configuration)
         applyConfigFromSystemProperties(configuration)
         applyConfigFromAutoScan(configuration)
         (detectAbstractProjectConfigs() + projectConfigs).forEach { applyConfigFromProjectConfig(it, configuration) }
         initialized = true
      }
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

/**
 * Scan the classpath for [AbstractProjectConfig] instances.
 * Only applies on the JVM.
 */
expect fun detectAbstractProjectConfigs(): List<AbstractProjectConfig>

