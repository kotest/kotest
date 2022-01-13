package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig

/**
 * Load an [AbstractProjectConfig] instance by using FQN from a system property.
 * Only applies on the JVM.
 */
internal expect fun loadProjectConfigFromClassname(): AbstractProjectConfig?
