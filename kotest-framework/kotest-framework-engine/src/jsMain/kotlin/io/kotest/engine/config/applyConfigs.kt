package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.Configuration

/**
 * Uses system properties to load configuration values onto the supplied [Configuration] object.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
actual fun applyConfigFromSystemProperties(configuration: Configuration) {}

/**
 *
 * Applies listeners, filters and extensions detected during scanning, that are annotated
 * with the [AutoScan] annotation.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
actual fun applyConfigFromAutoScan(configuration: Configuration) {}

/**
 * Modifies configuration with some defaults based on the platform.
 *
 * For example on JVM it will add System property based tag detection.
 */
actual fun applyPlatformDefaults(configuration: Configuration) {}

/**
 * Scan the classpath for [AbstractProjectConfig] instances.
 * Only applies on the JVM.
 */
actual fun detectAbstractProjectConfigs(): List<AbstractProjectConfig> = emptyList()
