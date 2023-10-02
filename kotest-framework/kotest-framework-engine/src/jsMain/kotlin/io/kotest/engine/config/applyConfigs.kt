package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration

/**
 * Uses system properties to load configuration values onto the supplied [ProjectConfiguration] object.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
internal actual fun applyConfigFromSystemProperties(configuration: ProjectConfiguration) {}

/**
 *
 * Applies listeners, filters and extensions detected during scanning, that are annotated
 * with the [AutoScan] annotation.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
internal actual fun applyConfigFromAutoScan(configuration: ProjectConfiguration) {}

/**
 * Modifies configuration with some defaults based on the platform.
 *
 * For example on JVM it will add System property based tag detection.
 */
internal actual fun applyPlatformDefaults(configuration: ProjectConfiguration) {}

/**
 * Scan the classpath for [AbstractProjectConfig] instances.
 * Only applies on the JVM.
 */
internal actual fun detectAbstractProjectConfigs(): List<AbstractProjectConfig> = emptyList()

/**
 * Only applies on the JVM.
 */
internal actual fun loadProjectConfigFromClassname(): AbstractProjectConfig? = null
