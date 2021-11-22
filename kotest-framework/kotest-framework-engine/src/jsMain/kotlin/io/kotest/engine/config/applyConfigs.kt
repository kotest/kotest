package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig

/**
 * Uses system properties to load configuration values onto the supplied [MutableConfiguration] object.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
internal actual fun applyConfigFromSystemProperties(configuration: MutableConfiguration) {}

/**
 *
 * Applies listeners, filters and extensions detected during scanning, that are annotated
 * with the [AutoScan] annotation.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
internal actual fun applyConfigFromAutoScan(configuration: MutableConfiguration) {}

/**
 * Modifies configuration with some defaults based on the platform.
 *
 * For example on JVM it will add System property based tag detection.
 */
internal actual fun applyPlatformDefaults(configuration: MutableConfiguration) {}

/**
 * Scan the classpath for [AbstractProjectConfig] instances.
 * Only applies on the JVM.
 */
internal actual fun detectAbstractProjectConfigs(): List<AbstractProjectConfig> = emptyList()
