package io.kotest.engine.config

import io.kotest.common.mapError
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import io.kotest.mpp.Logger
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object ConfigManager {

   private val logger = Logger(this::class)

   /**
    * Returns an initialized [ProjectConfiguration] by merging the given [MutableConfiguration] instance along with
    * supplied project configs, settings derived from system properties, extensions annotated with @AutoScan,
    * and project configs found on the classpath.
    *
    * @return the initialized input
    */
   fun initialize(configuration: MutableConfiguration, projectConfigs: List<AbstractProjectConfig>): ProjectConfiguration {
      compile(configuration, projectConfigs).getOrThrow()
      return configuration.toConfiguration()
   }

   private fun compile(configuration: MutableConfiguration, projectConfigs: List<AbstractProjectConfig>) = runCatching {
      logger.log { Pair(null, "compiling config projectConfigs=$projectConfigs") }
      applyPlatformDefaults(configuration)
      applyConfigFromSystemProperties(configuration)
      applyConfigFromAutoScan(configuration)
      projectConfigs.forEach { applyConfigFromProjectConfig(it, configuration) }
   }.mapError { ConfigurationException(it) }
}

class ConfigurationException(cause: Throwable) : Exception(cause)

/**
 * Uses system properties to load configuration values onto the supplied [MutableConfiguration] object.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
internal expect fun applyConfigFromSystemProperties(configuration: MutableConfiguration)

/**
 * Modifies configuration with some defaults based on the platform.
 *
 * For example on JVM it will add System property based tag detection.
 */
internal expect fun applyPlatformDefaults(configuration: MutableConfiguration)

/**
 *
 * Applies listeners, filters and extensions detected during scanning, that are annotated
 * with the [AutoScan] annotation.
 *
 * Note: This will only have an effect on JVM targets.
 */
internal expect fun applyConfigFromAutoScan(configuration: MutableConfiguration)

/**
 * Scan the classpath for [AbstractProjectConfig] instances.
 *
 * Note: This will only have an effect on JVM targets.
 */
internal expect fun detectAbstractProjectConfigs(): List<AbstractProjectConfig>

