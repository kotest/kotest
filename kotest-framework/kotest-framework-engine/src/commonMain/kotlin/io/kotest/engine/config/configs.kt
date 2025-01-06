package io.kotest.engine.config

import io.kotest.common.JVMOnly
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension

/**
 * Loads [Extension]s that should be applied automatically based on the platform.
 *
 * For example on JVM it will add a System property based tag detection.
 */
internal expect fun loadPlatformDefaultExtensions(): List<Extension>

/**
 * Load an [AbstractProjectConfig] instance using reflection from a well defined fully qualified classname.
 *
 * Only applies on the JVM.
 */
@JVMOnly
internal expect fun loadProjectConfigFromClassname(): AbstractProjectConfig?
