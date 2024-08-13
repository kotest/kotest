package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration

internal actual fun applyConfigFromSystemProperties(configuration: ProjectConfiguration) {}

internal actual fun applyPlatformDefaults(configuration: ProjectConfiguration) {}

internal actual fun detectAbstractProjectConfigs(): List<AbstractProjectConfig> = emptyList()

internal actual fun loadProjectConfigsFromClassname(): List<AbstractProjectConfig> = emptyList()
