package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.spec.Spec

/**
 * Returns the [AbstractPackageConfig]s for the given [spec], if any.
 * Configs are loaded from the package of the spec and any parent packages.
 *
 * This is a JVM only function. On other platforms, will return null.
 */
internal expect fun loadPackageConfigs(spec: Spec): List<AbstractPackageConfig>
