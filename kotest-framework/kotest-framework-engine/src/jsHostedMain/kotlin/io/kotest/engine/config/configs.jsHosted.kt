package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension

/**
 * Modifies configuration with some defaults based on the platform.
 *
 * For example on JVM it will add System property based tag detection.
 */
internal actual fun loadPlatformDefaultExtensions(): List<Extension> = emptyList()

/**
 * Only applies on the JVM.
 */
internal actual fun loadProjectConfigFromReflection(): AbstractProjectConfig? = null

internal actual fun loadPackageConfig(packageName: String): AbstractPackageConfig? = null

internal actual fun loadSystemPropertyConfiguration(): SystemPropertyConfiguration = NoopSystemPropertyConfiguration
